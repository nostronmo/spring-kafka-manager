from sanic import Sanic
from sanic_ext import Extend

web = Sanic("system")
_ = Extend(web)
