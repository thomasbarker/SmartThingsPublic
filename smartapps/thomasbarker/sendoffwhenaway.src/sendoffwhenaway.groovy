/**
 *  SendOffWhenAway
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
    name: "SendOffWhenAway",
    namespace: "thomasbarker",
    author: "Tom",
    description: "Actually send an off regardless of device status",
    category: "Mode Magic",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Setup") 
    {            
        input "switchpanel", "capability.switch", required: true,
            title: "Which switch?"
    }

}

def installed() 
{
	log.trace "installed"
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() 
{
	log.trace "updated"
	log.debug "Updated with settings: ${settings}"

    initialize()
}

def initialize() 
{  
	log.trace "initialize"
	unsubscribe()
    //subscribe(switchpanel, "switch", switchchange)
    subscribe(location, "mode", modeChangeHandler)
}


def modeChangeHandler(evt) 
{
	log.trace "modeChangeHandler"
    log.debug "mode changed to ${evt.value}"
    
    if(evt.value.equalsIgnoreCase("away") )
    {
    	log.debug "Change hit"
    	off()
    }
    
}

def on()
{
	log.debug "on"
    switchpanel.on()
}

def off()
{
	log.debug "off"
    switchpanel.off()
}


