import json
import os
from os import path


def rec(data, id):
    node = data["mapping"][id]
    msg = node["message"]
    print(f"ID: {id}, Message: {', '.join(part for part in msg['content']['parts']) if msg and 'content' in msg and 'parts' in msg['content'] else 'None'}")
    if "children" in node:
        for child_id in node["children"]:
            rec(data, child_id)

gptfolder = rf'gptchats'
with open(path.join(gptfolder, 'example.json'), 'r') as f:
    data = json.load(f)
    rec(data, "client-created-root")
    
    
