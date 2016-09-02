// USEFUL:
//https://github.com/sidjohn1/smartthings/blob/master/ThinkingCleaner.groovy


/**
 *  Roomba
 *
 *  Copyright 2016 FailedInsider
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
metadata 
	{
		definition (name: "Roomba", namespace: "FailedInsider", author: "FailedInsider") 
        {
            capability "Battery"
            capability "Polling"
            capability "Refresh"
            capability "Switch"
            capability "Tone"
            attribute "mode", "enum",["cleaning","docked", "paused", "stopped"]
        }
        
		preferences 
        {
			input("username", "text", title: "username", description: "username", required: true, displayDuringSetup: true)
			input("password", "text", title: "password", description: "password", defaultValue: "pass", required: true, displayDuringSetup: true)
		}
		



        simulator 
        {
        	status "on":  "command: 2003, payload: FF"
            //status "cleaning": "mode: cleaning"
            //status "docked": "mode: docked"
        }

        tiles
        {
valueTile("battery", "device.battery", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:' \u0003\u0003\u0003\u0003\u0003 \u0003\u0003\u0003\u0003\u0003 ...${currentValue}% \u0003\u0003\u0003\u0003', icon:"st.samsung.da.RC_ic_charge", backgroundColors: [
				[value: 20, color: "#bc2323"],
				[value: 50, color: "#ffff00"],
				[value: 96, color: "#79b821"]
			])
		}
		standardTile("beep", "device.beep", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state "beep", label:'beep', action:"tone.beep", icon:"st.quirky.spotter.quirky-spotter-sound-on", backgroundColor:"#ffffff"
		}
		standardTile("bin", "device.bin", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:'unknown', icon: "st.unknown.unknown.unknown")
			state ("empty", label:'Bin Empty', icon: "st.Kids.kids10", backgroundColor: "#79b821")
			state ("full", label:'Bin Full', icon: "st.Kids.kids19", backgroundColor: "#bc2323")
		}
		standardTile("clean", "device.switch", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state("on", label: 'dock', action: "switch.off", icon: "st.Appliances.appliances13", backgroundColor: "#79b821", nextState:"off")
			state("off", label: 'clean', action: "switch.on", icon: "st.Appliances.appliances13", backgroundColor: "#79b821", nextState:"on")
		}
		standardTile("network", "device.network", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:'unknown', icon: "st.unknown.unknown.unknown")
			state ("Connected", label:'Link Good', icon: "st.Appliances.appliances13", backgroundColor: "#79b821")
			state ("Not Connected", label:'Link Bad', icon: "st.Appliances.appliances13", backgroundColor: "#bc2323")
		}
		standardTile("spot", "device.spot", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state("spot", label: 'spot', action: "spot", icon: "st.Appliances.appliances13", backgroundColor: "#79b821")
		}
		standardTile("refresh", "device.switch", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state("default", label:'refresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon")
		}
		standardTile("status", "device.status", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:'unknown', icon: "st.unknown.unknown.unknown")
			state ("charging", label:'${currentValue}', icon: "st.Appliances.appliances13", backgroundColor: "#E5E500")
			state ("cleaning", label:'${currentValue}', icon: "st.Appliances.appliances13", backgroundColor: "#79b821")
			state ("docked", label:'${currentValue}', icon: "st.quirky.spotter.quirky-spotter-plugged", backgroundColor: "#79b821")
			state ("docking", label:'${currentValue}', icon: "st.Appliances.appliances13", backgroundColor: "#E5E500")
			state ("error", label:'${currentValue}', icon: "st.Appliances.appliances13", backgroundColor: "#bc2323")
			state ("waiting", label:'${currentValue}', icon: "st.Appliances.appliances13")
		}
		main("clean")
			details(["clean","spot","status","battery","bin","network","beep","refresh"])
		}
            
        


	} // end metadata

// parse events into attributes
def parse(String description) 
{
	log.debug "Parsing '${description}'"
    
    def attrName = null
    def attrValue = null
    
    if (description?.startsWith("mode:")) 
    {
        log.debug "mode command"
        attrName = "mode"
        attrValue = "docked"
        def result = createEvent(name: attrName, value: attrValue)

        log.debug "Parse returned ${result?.descriptionText}"
        return result
        
    }


}