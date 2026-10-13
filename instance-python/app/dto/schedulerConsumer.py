from typing import List, Optional

from pydantic import BaseModel


class ScrappingConsumer(BaseModel):
    timer: List[str] = None
    target: Optional[str] = "all"
