from __future__ import annotations

import json
import os
from typing import List, Any


def folder_exist_create(folder_path) -> None:
    if not os.path.exists(folder_path):
        os.makedirs(folder_path)


def read_to_list(source_path) -> list[Any] | None:
    if os.path.exists(source_path):
        cbx_o = open(source_path, 'rb')

        c_l = cbx_o.read()
        cj = json.loads(c_l)

        cbx_o.close()
        return list(cj)
    else:
        return None


def json_to_file(val, path, df=None) -> None:
    d = json.dumps(val, default=df, indent=4)
    data_to_file(d, path)


def data_to_file(val, path) -> None:
    r = open(path, 'w')
    r.write(val)
    r.close()


def file_to_tuple(path) -> tuple:
    t = open(path, 'r')
    r = t.read()
    e = eval(r)
    return e
