import json


import os
import json

gptfolder = rf'gptchats'

# Ensure the folder exists
os.makedirs(gptfolder, exist_ok=True)

with open(rf"gptchats/conversations.json", "r") as chat:
    jsn = json.load(chat)
    print(jsn[0])
    
with open(rf"gptchats/example.json", "w") as f:
    json.dump(jsn[0], f, indent=2)