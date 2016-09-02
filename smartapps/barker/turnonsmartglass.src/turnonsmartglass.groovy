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
        input "offsetsunrise", "number", title: "Turn on this many minutes before sunset"
        
    }
    section("For Whom?"){
		input "people", "capability.presenceSensor", title: "Select Person", required: true, multiple: true
	}
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(location, "sunsetTime", sunsetTimeHandler) 
    // We should only need to have it triggered once.
    //subscribe(location, "sunriseTime", sunriseTimeHandler)
    
    subscribe(people, "presence", presence)


    //schedule it to run today too
    scheduleTurnOn(location.currentValue("sunsetTime"),location.currentValue("sunriseTime"))
    
    def presenceState = people.currentState("present")
       
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
    
    if(now.time >= (ssss.sunrise.time - (offsetsunrise * 60 * 1000)))
    {
    	log.info "Turning on as someone came home"
    	turnOn()
    }
}



def sunsetTimeHandler(evt) {
    //when I find out the sunset time, schedule the lights to turn on with an offset
    scheduleTurnOn(location.currentValue("sunsetTime"),location.currentValue("sunriseTime"))
}



def scheduleTurnOn(sunsetString, sunriseString) 
{

    //get the Date value for the string
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
    def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)

    //calculate the offset
    def timeBeforeSunset = new Date(sunsetTime.time - (offsetsunset * 60 * 1000))
    def timeBeforeSunrise = new Date(sunriseTime.time - (offsetsunrise * 60 * 1000))

    log.debug "Scheduling for: $timeBeforeSunset (sunset is $sunsetTime)"
    log.debug "Scheduling for: $timeBeforeSunrise (sunrise is $sunriseTime)"

    //schedule this to run one time
    runOnce(timeBeforeSunset, turnOn)
    runOnce(timeBeforeSunrise, turnOff)
}



def turnOn() {
    log.debug "turning on"
    switches.on()
}

def turnOff() {
    log.debug "turning off"
    switches.off()
}