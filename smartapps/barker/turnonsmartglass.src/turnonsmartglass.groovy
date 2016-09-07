/**
 *  Turnonsmartglass
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
    name: "Turnonsmartglass",
    namespace: "barker",
    author: "Tom",
    description: "dec",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Switches") {
        input "switches", "capability.switch", title: "Which lights to turn on?", multiple: true 
        input "offsetsunset", "number", title: "Turn off this many minutes before sunset"
        input "offsetsunrise", "number", title: "Turn on this many minutes before sunrise"
        
    }
    section("For Whom?"){
		input "people", "capability.presenceSensor", title: "Select Person", required: true, multiple: true
	}
    
    	section( "Notifications" ) {
        input("recipients", "contact", title: "Send notifications to") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phoneNumber", "phone", title: "Send a text message?", required: false
        }
	}
    
}

def installed() {
log.debug "installed"
    initialize()
}

def updated() {
log.debug "updated"
    initialize()
}

def initialize() {
    unsubscribe()
	subscribe(location, "sunsetTime", sunsetTimeHandler) 
    //subscribe(location, "sunriseTime", sunriseTimeHandler)
    
    subscribe(people, "presence", presence)


    //schedule it to run today
    scheduleTurnOn(location.currentValue("sunsetTime"),location.currentValue("sunriseTime"))
    
    def presenceState = people.currentState("present")
       
}


private peoplePresent()
{   
	def peoplePresent = 0
     
    // iterate over our people variable that we defined
    // in the preferences method
    for (person in people) 
    {
        if (person.currentPresence == "present") 
        {
            peoplePresent++
                }
    }
    return peoplePresent
}

def presence(evt) {
    
	def peoplePresent = 0
     
    // iterate over our people variable that we defined
    // in the preferences method
    for (person in people) 
    {
        if (person.currentPresence == "present") 
        {
            peoplePresent++
                }
    }

    if( state.previousPeoplePresent == 0 && peoplePresent > 0)
    {
        log.info "First entry into the house arrived"
        TurnOnTest()
    }



    
    state.previousPeoplePresent = peoplePresent

}

def TurnOnTest()
{
    def now = new Date()
    def ssss = getSunriseAndSunset()
    
    // only work with time, then no issue of today or tomorrow
    if( (now.time >= (ssss.sunrise.time - (offsetsunrise * 60 * 1000))) && (now.time < (ssss.sunset.time - (offsetsunset * 60 * 1000))))
    {
    	log.info "Turning on as someone came home"
    	turnOn()
    }
}



def sunsetTimeHandler(evt) {
    //when I find out the sunset time, schedule the lights to turn on with an offset

    if(peoplePresent() > 0)
    {
    	scheduleTurnOn(location.currentValue("sunsetTime"),location.currentValue("sunriseTime"))
	}
}
def sunriseTimeHandler(evt) {
    //when I find out the sunset time, schedule the lights to turn on with an offset
    
    if(peoplePresent() > 0)
    {
    	scheduleTurnOn(location.currentValue("sunsetTime"),location.currentValue("sunriseTime"))
	}
}


def scheduleTurnOn(sunsetString, sunriseString) 
{

    //get the Date value for the string
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
    def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)

    //calculate the offset
    def timeBeforeSunset = new Date(sunsetTime.time - (offsetsunset * 60 * 1000))
    def timeBeforeSunrise = new Date(sunriseTime.time - (offsetsunrise * 60 * 1000))

    log.debug "Scheduling sunset for: $timeBeforeSunset (sunset is $sunsetTime)"
    log.debug "Scheduling sunrise for: $timeBeforeSunrise (sunrise is $sunriseTime)"

   
	//unschedule()

    //schedule this to run one time
    runOnce(timeBeforeSunset, turnOff)
    runOnce(timeBeforeSunrise, turnOn)
    
    // modify time to local and notify
    timeBeforeSunset.time = timeBeforeSunset.time + location.timeZone.getRawOffset() + location.timeZone.getDSTSavings()
    timeBeforeSunrise.time = timeBeforeSunrise.time + location.timeZone.getRawOffset() + location.timeZone.getDSTSavings()
    send("Scheduling sunset for: $timeBeforeSunset")
    send("Scheduling sunrise for: $timeBeforeSunrise")
    
}



def turnOn() {
    log.debug "turning on"
    switches.on()
}

def turnOff() {
    log.debug "turning off"
    switches.off()
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

}