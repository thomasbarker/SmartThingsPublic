/**
 *  Quick Device Check SmartApp for SmartThings
 *
 *  Copyright 2015 J.Constantelos
 *
 *  Licen sed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Contributors
 *  ----------------
 *  Based off of code from Brandon Gordon (https://github.com/notoriousbdg) - BatteryMonitor SmartApp (https://github.com/notoriousbdg/SmartThings.BatteryMonitor)
 *
 *  Overview
 *  ----------------
 *  This SmartApp helps you see a quick status of selected devices by checking to see if at least one event exists for the selected device(s).
 *  If no events are found:
 *  	1. You have a device that hasn't been used or reported any event, like infrequently used switches.  These may not have dropped from your network.
 *		2. You have a device that may still be working, like temp, motion, or humidity sensors, but has dropped of your network for some reason.
 *
 *  Notes
 *  ----------------
 *  - SmartThings only keeps events for 7 days if I remember right.
 *  - Refreshing a device will cause an event, which could cause you to think it's working when it's not.
 *  - This app works best with devices that you would always expect to get some type of event - like motion, temp, humidity, Lux, battery, etc. but you personally don't check up on that often.
 *  - This app also works best with devices you frequently use, like wall switches, bulbs, etc.
 *
 *  Revision History
 *  ----------------
 *  2015-11-04  v1.0.0  Initial release
 *  2015-11-07  v1.0.1  Added Smoke/CO2 detectors to the list of available devices to check
 *	2016-02-19  v2.0	Add ability to send notifications / texts if something hasn't checked in (Craig Lyons) 
 */

definition(
    name: "Dashboard - Device Check",
    namespace: "Craig.K.Lyons",
    author: "jscgs350",
    description: "SmartApp to show a quick status of selected devices by seeing if at least 1 event exists.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page name:"pageStatus"
	page name:"pageConfigure"
	}

//***************************
//Show Status page
//***************************
def pageStatus() {
	unsubscribe()
    subscribeDevices()
    
    runIn(60*5, pageStatus)
    
    def pageProperties = [
		name:       "pageStatus",
		title:      "Quick Device Check",
		nextPage:   null,
		install:    true,
		uninstall:  true
	]

	if (settings.motiondevices == null
    	&& settings.humiditydevices == null
        && settings.leakdevices == null
        && settings.thermodevices == null
        && settings.tempdevices == null
        && settings.contactdevices == null
        && settings.lockdevices == null
        && settings.alarmdevices == null
        && settings.switchdevices == null
        && settings.smokedevices == null
        && settings.presencedevices == null) {
			return pageConfigure()
	}
    
	return dynamicPage(pageProperties) {
    	
	    if(!state.checkRunning)
        {
        	state.checkRunning = true
            
            doCheck()
        
    
            if (state.goodlist) {
                section("Devices Reporting (hrs old)") {
                    paragraph state.goodlist.trim()
                }
            }

            if (state.badlist) {
                section("Devices NOT Reporting Events") {
                    paragraph state.badlist.trim()
                }
            }

            if (state.errorlist) {
                section("Devices with Errors") {
                    paragraph state.errorlist.trim()
                }
            }

            section("Menu") {
                href "pageStatus", title:"Refresh", description:"Tap to refresh the status of devices"
                href "pageConfigure", title:"Configure", description:"Tap to manage your list of devices"
            }
        	
            state.checkRunning = false
            
       	}
	}    
}

def doCheck(evt){
	doCheck()
}
    
def doCheck () {
        
        def rightNow = new Date()
        
    def goodlist = ""
	def badlist = ""
	def errorlist = ""
    def delaylist = ""
    def delayListCheck = ""
    
        settings.motiondevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
		settings.humiditydevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
		settings.leakdevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
		settings.thermodevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
        
        settings.tempdevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
        
        settings.contactdevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                	
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"

                    }
                    
                    goodlist += "$xhours: $it.displayName\n"
                    log.info "Contact State: '${state.goodlist}'"
                    
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
        
        settings.lockdevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
		settings.alarmdevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
		settings.switchdevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
		settings.presencedevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
					
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
		settings.smokedevices.each() {
			def lastTime = it.events(max: 1).date
			try {
				if (lastTime) {
                    def hours = (((rightNow.time - lastTime.time) / 60000) / 60)
            		def xhours = (hours.toFloat()/1).round(2)
                    
                    if (xhours > timer){
                    	def thours = (hours.toFloat()/1).round(0)
                        delaylist += "$it.displayName ($thours)\n"
                        delayListCheck += "$it.displayName\n"
                    }
                    goodlist += "$xhours: $it.displayName\n"
				} else {
					badlist += "$it.displayName\n"	
				}

			} catch (e) {
					log.trace "Caught error checking a device."
					log.trace e
					errorlist += "$it.displayName\n"
			}
		}
        
        
        if ((badlist || errorlist) && sendNotification == "Yes")
        {
 			
            
            log.trace "Preparing Notification"
            
            def text  = ""
            def check = ""
            
            	if(delaylist)
                {
                	text += "Devices delayed:\n${delaylist.trim()}\n\n"
                    check += "${delayListCheck.trim()}"
                }
                if (badlist)
                {
                	text += "Devices Not Reporting Events:\n${badlist.trim()}\n\n"
                    check += "${badlist.trim()}"
                }
              	if (errorlist)
                {
                	text += "Devices with Errors:\n${state.errorlist.trim()}\n\n"
                    check += "${errorlist.trim()}"
                }
                
                //log.info "Check: ${check}"
                //log.info "lastText: ${state.lastText}"
                //log.trace "check vs. lastText"
                
                if(sendNotification == 'Yes')
                {
                    if(check != state.lastText)
                    {
                        state.lastText = check
                        send(text)
                    }
                    else
                    {
                        log.trace "Same error message already sent"
                    }
                }
            
        }
        
    state.goodlist = goodlist
    state.badlist = badlist
	state.errorlist = errorlist
    state.delaylist = delaylist
    state.delayListCheck = delayListCheck
        
}

//***************************
//Show Configure Page
//***************************
def pageConfigure() {
	def helpPage = "Select devices that you wish to check when you open this SmartApp."

	def inputMotionDevices = [name:"motiondevices",type:"capability.motionSensor",title:"Which motion sensors?",multiple:true,required:false]
	def inputHumidityDevices = [name:"humiditydevices",type:"capability.relativeHumidityMeasurement",title:"Which humidity sensors?",multiple:true,required:false]
	def inputLeakDevices = [name:"leakdevices",type:"capability.waterSensor",title:"Which leak sensors?",multiple:true,required:false]
	def inputThermoDevices = [name:"thermodevices",type:"capability.thermostat",title:"Which thermostats?",multiple:true,required:false]
	def inputTemperature = [name:"tempdevices",type:"capability.temperatureMeasurement",title:"Which temperature sensors?",multiple:true,required:false]
	def inputContactDevices = [name:"contactdevices",type:"capability.contactSensor",title:"Which open/close contact sensors?",multiple:true,required:false]
	def inputLockDevices = [name:"lockdevices",type:"capability.lock",title:"Which locks?",multiple:true,required:false]
    def inputAlarmDevices = [name:"alarmdevices",type:"capability.alarm",title:"Which alarms/sirens?",multiple:true,required:false]
    def inputSwitchDevices = [name:"switchdevices",type:"capability.switch",title:"Which switches?",multiple:true,required:false]
    def inputPresenceDevices = [name:"presencedevices",type:"capability.presenceSensor",title:"Which presence sensors?",multiple:true,required:false]
    def inputSmokeDevices = [name:"smokedevices",type:"capability.smokeDetector",title:"Which Smoke/CO2 detectors?",multiple:true,required:false]
	
	def pageProperties = [name:"pageConfigure",
		title:          "Quick Device Check Configurator",
		nextPage:       "pageStatus",
		uninstall:      true
	]

	return dynamicPage(pageProperties) {
		section("About This App") {
			paragraph helpPage
		}
                    
        section("Send sms (leave blank for push)") {
    	input "sendNotification", "enum", title: "Send Notification?", required: true, options: ["Yes", "No"]
        input "phoneNumber", "phone", title: "Phone number", required: false
        input "timer", "number", title: "How long (in hours) before a device is considered inactive?", required: false
    }

		section("Devices To Check") {
			input inputMotionDevices
			input inputHumidityDevices
			input inputLeakDevices
			input inputThermoDevices
            input inputTemperature
			input inputContactDevices
            input inputLockDevices
            input inputAlarmDevices
            input inputSwitchDevices
            input inputPresenceDevices
            input inputSmokeDevices
		}

		section([title:"Available Options", mobileOnly:true]) {
			label title:"Assign a name for your app (optional)", required:false
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
	state.checkRunning = false
	log.trace "Launching Quick Device Check"
    subscribeDevices()
}

def subscribeDevices(){

	log.trace "subscribing to Devices"
    subscribe(motiondevices, "motion", doCheck)
    subscribe(humiditydevices, "relativeHumidity", doCheck)
    subscribe(leakdevices, "water", doCheck)
    subscribe(thermodevices, "Temperature", doCheck)
    subscribe(thermodevices, "heatingSetpoint", doCheck)
    subscribe(thermodevices, "coolingSetpoint", doCheck)
    subscribe(tempdevices, "temperature", doCheck)
    subscribe(contactdevices, "contact", doCheck)
    subscribe(lockdevices, "lock", doCheck)
    subscribe(alarmdevices, "alarm", doCheck)
    subscribe(switchdevices, "switch", doCheck)
    subscribe(presencedevices, "presence", doCheck)
    subscribe(smokedevices, "smokeDetector", doCheck)
    
}

def send(text) {
    sendPush(text)
    if (phoneNumber == null) {
    	
    }
    else {
        
        sendSms(phoneNumber, text) 
    }
}