/**
 *  CustomWelcomeHomeLights
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
    name: "CustomWelcomeHomeLights",
    namespace: "barker",
    author: "Tom",
    description: "dec",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Setup") {            
            
        input "motionsense", "capability.motionSensor", required: true,
            title: "Which motion sensor?" //"active" "inactive"
            
        input "frontDoor", "capability.contactSensor", 
        title: "Which door", required: true, multiple: false //"open" "closed"
        
        input "light", "capability.switch", required: true, multiple: true,
            title: "Which lights?"
    }

    section("Send Notifications?") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Warn with text message (optional)",
                description: "Phone Number", required: false
        }
    }
    

}



private checkSunset()
{

	def now = new Date()
	def ssss = getSunriseAndSunset(sunsetOffset: "-00:30")
    
    log.info "checking dates:"
    log.info "now: ${now.time}"
    log.info "sunrise: ${ssss.sunrise.time}"
    log.info "sunset: ${ssss.sunset.time}"
    
    // work only on times, skip the date part...
    if(now.time >= ssss.sunset.time)
    	log.info "greater than sunset"
        
    if(now.time <= ssss.sunrise.time)
    	log.info "less than sunrise"
    
    if(now.time >= ssss.sunset.time || now.time <= ssss.sunrise.time)
    {
    	log.info "yes it is past sunset and before sunrise"
        return true
    }
    else
    {
    	log.info "no it is not past sunset"
        return false
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
    //subscribe(switchpanel, "switch", switchchange)
    subscribe(frontDoor, "contact.open", contactHandler)
    subscribe(motionsense, "motion.active", motionHandler)
    state.contactFirst = false
    
    checkSunset()
}

def contactHandler(evt) 
{
    log.info evt
    if("open" == evt.value && checkSunset())
    {
    	log.info "setting state to true"
        state.contactFirst = true
        
        runIn(20, killContactFirst)
    }
    
}

def killContactFirst()
{
	log.info "****timer"
	state.contactFirst = false
}

def motionHandler(evt) 
{
    log.info state.contactFirst
    log.info evt.value
  if(state.contactFirst && "active" == evt.value) 
  {
  
  	log.info "turning light on"
    state.contactFirst = false
    light*.on()
  } 
  else
  {
  }
  

}