import asyncio
import json
import time
from datetime import timedelta

import sanic
from app.config.kafkaVariables import (
    KAFKA_PRODUCER,
    KAFKA_SERVER,
)
from app.config.webconfig import web
from app.service.syncJob import is_system_working
from confluent_kafka import Producer
from sanic.log import logger
from tenacity import AsyncRetrying, stop_after_attempt, wait_exponential

SYSTEM_START_TIME = time.time()

INTERVAL_SECONDS = 10

producer = Producer({"bootstrap.servers": KAFKA_SERVER})


async def system_status():
    if KAFKA_PRODUCER == "NONE":
        logger.error("Kafka topic KAFKA_SERVICES_PRODUCER is not configured.")
        return

    logger.info(f"Starting Kafka heartbeat: {INTERVAL_SECONDS}s")

    try:
        while True:
            try:
                async for attempt in AsyncRetrying(
                    wait=wait_exponential(multiplier=1, min=1, max=10),
                    stop=stop_after_attempt(5),
                    reraise=True,
                ):
                    with attempt:
                        formatted_uptime = str(
                            timedelta(seconds=int(time.time() - web.ctx.start_time))
                        )

                        system_status = check_system_status()

                        system_status = "running" if system_status else "stopped"

                        payload = {
                            "name": f"Webscrapping Service --{web.name}--",
                            "service": web.name,
                            "serviceState": system_status,
                            "checkpoints": "none",
                            "time": formatted_uptime,
                            "status": "online",
                            "framework": f"Sanic Async v{sanic.__version__}",
                        }

                        producer.produce(
                            KAFKA_PRODUCER, value=json.dumps(payload).encode("utf-8")
                        )
                        producer.poll(0)

            except Exception as e:  # noqa: BLE001
                logger.error(
                    f"Failed to send System Status to Kafka after retries: {e}"
                )
            await asyncio.sleep(INTERVAL_SECONDS)

    except asyncio.CancelledError:
        logger.info(
            "Sanic is shutting down. Flushing final Kafka heartbeat messages..."
        )
        producer.flush(timeout=5)


def check_system_status() -> bool:
    return is_system_working
