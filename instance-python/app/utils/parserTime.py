import re

from pydantic import BaseModel


def parse_timer_to_seconds(timer_str: str) -> int:
    timer_str = timer_str.strip().upper()

    if timer_str.endswith("H"):
        return int(timer_str[:-1]) * 3600
    elif timer_str.endswith("M"):
        return int(timer_str[:-1]) * 60
    elif timer_str.endswith("D"):
        return int(timer_str[:-1]) * 86400
    elif timer_str.endswith("S"):
        return int(timer_str[:-1])
    else:
        return int(timer_str)
