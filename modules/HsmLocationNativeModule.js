//  Created by react-native-create-bridge

import {NativeModules, NativeEventEmitter} from 'react-native';

const {HsmLocation} = NativeModules;

const locationEventEmitter = new NativeEventEmitter(HsmLocation);
/**
 * module to call native java methods for location updates, can be used when collecting location information in the background
 */
export default {
  lastLocation() {
    return HsmLocation.currentLocation();
  },
  EXAMPLE_CONSTANT: HsmLocation.EXAMPLE_CONSTANT,
  emitter: locationEventEmitter,
};
