/**
 *  VirtualLightSwitch
 *
 *  Copyright 2016 Tom
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "VirtualLightSwitch",
    namespace: "Barker",
    author: "Tom",
    description: "Virtual Lightswitch",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")



preferences {
	section("Setup") {            
        input "switchpanel", "capability.switch", required: true,
            title: "Which switch?"
            
        input "light", "capability.switch", required: true, multiple: true
            title: "Which lights?"
    }

    section("Send Notifications?") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Warn with text message (optional)",
                description: "Phone Number", required: false
        }
    }
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {  
	unsubscribe()
    subscribe(switchpanel, "switch", switchchange)
    subscribe(light, "switch", lightchange)
}

def on()
{
    light.on()
    switchpanel.on()
}

def off()
{
    light.off()
    switchpanel.off()
}

def lightchange(evt) {

	if(evt.value == "on")
    {
		on()
    }
    else
    {
		off()
    }
}

def switchchange(evt) {

	if(evt.value == "on")
    {
        on()
    }
    else
    {
        off()
    }
}
