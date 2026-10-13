import os

from confluent_kafka import Producer
from dotenv import load_dotenv
from sanic.log import logger

_ = load_dotenv()

KAFKA_SERVER = os.environ.get("KAFKA_SERVER", "localhost:9092")
KAFKA_CONSUMER = os.environ.get("KAFKA_SERVICES_TIMER", "NONE")
KAFKA_PRODUCER = os.environ.get("KAFKA_SERVICES_STATUS", "NONE")


def test_kafka_connection() -> bool:
    try:
        logger.info(f"Testing kafka connection: {KAFKA_SERVER}")
        KAFKA_CONFIG = {
            "bootstrap.servers": KAFKA_SERVER,
            "allow.auto.create.topics": False,
        }

        topics_metadata = Producer(KAFKA_CONFIG).list_topics(timeout=3.0)

        logger.info("Kafka Connected")
        logger.info(
            f"Topics on the Kafka Network: {list(topics_metadata.topics.keys())}"
        )
        return True
    except Exception as e:  # noqa: BLE001
        logger.error(f"Kafka is Offline. Reason: {e}")
        return False
