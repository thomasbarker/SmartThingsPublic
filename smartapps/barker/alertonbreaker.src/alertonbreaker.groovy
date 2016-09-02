/**
 *  AlertIfLostPowerOrBreakerFires
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
    name: "AlertonBreaker",
    namespace: "barker",
    author: "Tom",
    description: "that",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Setup") {            
            
        input "power", "capability.switch", required: true,
            title: "Which power socket to monitor?"
            
        input "lastupdater", "capability.estimatedTimeOfArrival", required: true,
            title: "Which last update?"
    }

	section( "Notifications" ) {
        input("recipients", "contact", title: "Send notifications to") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phoneNumber", "phone", title: "Send a text message?", required: false
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
//    subscribe(power, "switch", switchchange)
    subscribe(lastupdater, "eta", etachange)
    
    pingSensor()
    runEvery5Minutes(pingSensor)
    
}

def pingSensor(){
	log.debug "PingSensor"
	checkTimeDifference()
	power.on()
}

def checkTimeDifference()
{
    def lastdater = new Date(state.lastupdate)
	def eventdate = new Date()

    def diff = (eventdate-lastdater)
    
    long llastdate = lastdater.getTime()
    long leventdate = eventdate.getTime()

    
    llastdate = llastdate/1000
    leventdate = leventdate/1000
    
    long timeDiff = Math.abs(leventdate-llastdate)
    log.debug timeDiff
    
    
    log.debug "Current ping time difference is " + timeDiff + " seconds."
    if(timeDiff > (60*12)) // 12 minutes
    {
    	send("Condensate pump has tripped")
    }


}

def etachange(evt) {
    
    if(!state.lastupdate)
    {
    	log.debug "setting date"
    	state.lastupdate = evt.value
    }
    log.debug state.lastupdate
	

    state.lastupdate = evt.value
    
    
   
}



private send(msg) {

    if (location.contactBookEnabled) {
        log.debug("sending notifications to: ${recipients?.size()}")
        sendNotificationToContacts(msg, recipients)
    }
    else {
        if (sendPushMessage == "Yes") {
            log.debug("sending push message")
            sendPush(msg)
        }

        if (phoneNumber) {
            log.debug("sending text message")
            sendSms(phoneNumber, msg)
        }
    }

	log.debug msg
}
def switchchange(evt) {

	if(evt.value == "off")
    {
    	//send("Condensate power socket was turned off!")
        log.error "Condensate power socket was turned off!"
    
    }
}
