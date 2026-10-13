import asyncio
import os
import sys
import time

from app.config.kafkaVariables import (
    test_kafka_connection,
)
from app.config.webconfig import web
from app.kafka.dataConsumerKafka import kafka_background_worker
from app.kafka.dataProducerKafka import system_status
from app.service.syncJob import scheduled_system_job
from dotenv import load_dotenv
from sanic.log import logger

_ = load_dotenv()

import warnings

warnings.filterwarnings("ignore", category=DeprecationWarning)

IS_DEV = os.environ.get("DEV_MODE", "true").lower() in ["true", "1", "yes"]


@web.before_server_start
async def setup_app_state(app, loop):
    app.ctx.start_time = time.time()


async def reconnect_kafka_loop(app):
    logger.info("Starting Kafka reconnection watcher...")
    while True:
        status = test_kafka_connection()
        if status:
            logger.info("Kafka connection successful! Starting background workers...")
            app.add_task(kafka_background_worker(app))
            app.add_task(system_status())
            break

        logger.warning("Kafka still offline. Retrying in 30 seconds...")
        await asyncio.sleep(30)


@web.after_server_start
async def loop_after_app_start(app, loop):
    app.add_task(reconnect_kafka_loop(app))


# This is for testing
@web.after_server_start
async def setup_background_tasks(app, loop):
    if IS_DEV:
        logger.info("Starting default 1-Hour system schedule...")
        loop.create_task(scheduled_system_job())


if __name__ == "__main__":
    web.run(host="0.0.0.0", port=7021, dev=False, debug=IS_DEV, auto_reload=False)
