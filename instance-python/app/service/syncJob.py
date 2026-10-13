import asyncio

from sanic.log import logger

is_system_working = False


async def scheduled_system_job():
    global is_system_working
    is_system_working = True
    logger.info("System task initiated with interval sequence")

    try:
        _ = await asyncio.gather()

        logger.info("<<< System complete. Awaiting next Kafka command.")

    except asyncio.CancelledError:
        logger.info("The System task was gracefully cancelled by a STOP command.")
    finally:
        is_system_working = False
