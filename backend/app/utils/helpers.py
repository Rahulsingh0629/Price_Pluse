import random
import time

from app.config import settings


def random_user_agent() -> str:
    return random.choice(settings.user_agents)


def polite_delay() -> None:
    delay = random.uniform(settings.min_delay_seconds, settings.max_delay_seconds)
    time.sleep(delay)
