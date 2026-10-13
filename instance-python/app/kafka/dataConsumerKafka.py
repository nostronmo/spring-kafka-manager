import asyncio
import json

from app.config.kafkaVariables import KAFKA_CONSUMER, KAFKA_SERVER
from app.config.webconfig import web
from app.dto.schedulerConsumer import ScrappingConsumer
from app.service.syncJob import scheduled_system_job
from confluent_kafka import Consumer
from sanic.log import logger


async def kafka_background_worker(app):
    conf = {
        "bootstrap.servers": KAFKA_SERVER,
        "group.id": f"{web.name}-scheduler-group",
        "auto.offset.reset": "earliest",
    }

    consumer = Consumer(conf)
    consumer.subscribe([KAFKA_CONSUMER])

    logger.info(f"Consumer Initiated. Watching Topic '{KAFKA_CONSUMER}'")

    if not hasattr(app.ctx, "active_system_tasks"):
        app.ctx.active_system_tasks = {}

    while True:
        try:
            message = await asyncio.to_thread(consumer.poll, 0.1)

            if message is None:
                continue

            raw_key = message.key()
            scheduler_id = raw_key.decode("utf-8") if raw_key else "unknown"

            complete_data = message.value()
            if not complete_data or not complete_data.strip():
                logger.warning("Empty or whitespace-only message received. Skipping.")
                continue

            try:
                decoded_str = complete_data.decode("utf-8").strip()
                parsed_json = json.loads(decoded_str)
                formatted_body = ScrappingConsumer(**parsed_json)
            except json.JSONDecodeError:
                logger.warning(
                    f"Ignored malformed Kafka message (not valid JSON): {complete_data}"
                )
                continue

            target = (getattr(formatted_body, "target", None) or "all").lower()
            current_app_name = web.name.lower()

            if target != "all" and target != current_app_name:
                continue

            active_tasks = app.ctx.active_system_tasks

            if not formatted_body.timer:
                if scheduler_id in active_tasks:
                    logger.info(
                        f"STOP command received. Cancelling Scrape ID: {scheduler_id}"
                    )
                    task = active_tasks[scheduler_id]
                    if not task.done():
                        task.cancel()
                    del active_tasks[scheduler_id]

            # Start New Schedule
            else:
                loop = asyncio.get_running_loop()

                if (
                    scheduler_id in active_tasks
                    and not active_tasks[scheduler_id].done()
                ):
                    logger.warning(
                        f"Previous scrape {scheduler_id} still running. Cancelling to restart."
                    )
                    active_tasks[scheduler_id].cancel()

                logger.info(f"START command received for Scheduler ID: {scheduler_id}")
                task = loop.create_task(scheduled_system_job())
                active_tasks[scheduler_id] = task

                task.add_done_callback(
                    lambda t, sid=scheduler_id: active_tasks.pop(sid, None)
                )

        except Exception as e:  # noqa: BLE001
            logger.error(f"Critical Error on Kafka: {e}")
            await asyncio.sleep(1)
