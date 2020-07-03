/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';

import MapBridgeView from './mapComponents/MapBridgeNativeView';

import fakeDB from './fakeDB.json';

export default class App extends Component {
  markersList;

  mSelectedMarker = {
    title: '!',
    snippet: '!',
    lat: 0,
    lng: 0,
  };

  constructor(props) {
    super(props);
    this.state = {
      markers: [],
      selectedMarker: this.mSelectedMarker,
      isReady: false,
      isMounted: false,
    };
  }

  /**
   * load the marker with a delay to simulate an HTTP GET request, if the is ready
   */
  loadMarkers() {
    if (!this.state.isReady) {
      return;
    }
    setTimeout(() => {
      this.markersList = this.getMarkersList();

      console.log(
        'loadMarker setState',
        this.markersList.length,
        this.markersList,
      );
      this.setState({
        markers: this.markersList,
        selectedMarker: this.markersList[3],
      });
    }, 0);
  }

  /**
   * callback function gets called when the map is ready
   * you can call methods to show markers here
   */
  onMapReady = () => {
    console.log('map is ready');

    this.setState({
      isReady: true,
    });
  };

  /**
   * return a list of markers acquired from ./fadeDB.json with randomized lat(latitude) and lng(longitude) values to simulate data update
   */
  getMarkersList() {
    return fakeDB.map((marker, i) => ({
      id: i,
      title: marker.title,
      snippet: marker.snippet,
      lat: randLat(),
      lng: randLng(),
    }));
  }

  /**
   * function will be invoked when a marker in the map is clicked
   */
  onMarkerTap = marker => {
    console.log('log', 'HsmMap OnMapTap', marker);
    console.log('onMarkerTap setState');

    this.setState({
      selectedMarker: marker,
    });
  };

  /**
   * called on button press
   * updates the state to tell java to move the map's camera to a location
   *  determined by latitude and longitude of the previous marker in the list
   */
  prevMarker() {
    const i = this.state.selectedMarker.id;

    console.log('nav', this.markersList, i);
    if (!this.markersList || !this.markersList[i - 1]) {
      return;
    }

    console.log('prevMarker setState', i, this.markersList[i - 1]);

    this.setState({
      selectedMarker: this.markersList[i - 1],
    });
  }

  /**
   * called on button press
   * updates the state to tell java to move the map's camera to a location
   *  determined by latitude and longitude of the next marker in the list
   */
  nextMarker() {
    const i = this.state.selectedMarker.id;

    if (!this.markersList || !this.markersList[i + 1]) {
      return;
    }

    console.log('nextMarker setState', i, this.markersList[i + 1]);

    this.setState({
      selectedMarker: this.markersList[i + 1],
    });
  }

  /**
   * called on button press
   * updates the state to tell java to show the current location on the map
   */
  showMyLocation() {
    console.log('show my location');
    this.setState({
      showCurrentLocation: true,
    });
  }

  /**
   * change the state to notify java to call to appropriate lifecycle method of the map, to save resources
   */
  componentWillUnmount() {
    this.setState({
      isMounted: false,
    });
  }

  /**
   * return view
   */
  render() {
    return (
      <>
        <StatusBar barStyle="dark-content" />
        <View style={styles.body}>
          {/* START: huawei MAP */}
          <MapBridgeView
            style={styles.map}
            showCurrentLocation={this.state.showCurrentLocation}
            onMapReady={this.onMapReady}
            markerIcon="https://webstockreview.net/images250_/clipart-heart-map-4.png"
            selectedMarker={this.state.selectedMarker}
            onMarkerTap={this.onMarkerTap}
            markers={this.state.markers}
          />
          {/* END: huawei Map */}

          {/* START: MAIN BUTTONS */}
          <View style={styles.buttonsWrapper}>
            <Button title="load markers" onPress={() => this.loadMarkers()} />
            <Button
              title="show current location"
              onPress={() => this.showMyLocation()}
            />
          </View>
          {/* END: MAIN BUTTONS */}

          {/* START: NAVIGATION BUTTONS */}
          <View style={styles.nav}>
            <Button
              style={styles.navButton}
              title="↑"
              onPress={() => this.prevMarker()}
            />
            {/* START: SPACER */}
            <View style={{width: 10}} />
            {/* END: SPACER */}
            <Button
              style={styles.navButton}
              title="↓"
              onPress={() => this.nextMarker()}
            />
          </View>
          {/* END: NAVIGATION BUTTONS */}

          {/* START: MARKER DETAILS */}
          <View style={styles.sectionContainer}>
            <Text>
              Map state: {this.state.isReady ? 'READY' : 'NOT READY!'}
            </Text>
            <Text style={styles.sectionTitle}>Selected Marker info: </Text>

            <Text style={styles.highlight}>
              title: {this.state.selectedMarker.title}
            </Text>
            <Text style={styles.highlight}>
              snippet: {this.state.selectedMarker.snippet}
            </Text>
            <Text style={styles.highlight}>
              Position:( {this.state.selectedMarker.lat} {' , '}
              {this.state.selectedMarker.lng})
            </Text>
          </View>
          {/* END: MARKER DETAILS */}
        </View>
      </>
    );
  }
}

/**
 * return a random latitude value near dubai
 */
function randLat() {
  return randomCoord(24, 25.5);
}

/**
 * returns a random longitude value near dubai
 */
function randLng() {
  return randomCoord(55, 56);
}

/**
 *  returns a random float value in a range
 * @param {int} from start of  range, lowerBound
 * @param {*} to end of range, upperBound
 */
function randomCoord(from, to) {
  return (Math.random() * (to - from) + from).toFixed(5) * 1;
  // .toFixed() returns string, so ' * 1' is a trick to convert to number
}

/**
 * styles for the views
 */
const styles = StyleSheet.create({
  map: {
    width: '100%',
    minHeight: '65%',
    borderWidth: 2,
  },
  scrollView: {
    paddingTop: 50,
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    borderWidth: 2,
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    paddingHorizontal: 16,
    height: '20%',
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  nav: {
    flexDirection: 'row',
    marginHorizontal: 12,
    marginTop: 10,
    alignSelf: 'flex-end',
    alignContent: 'center',
  },
  highlight: {
    flex: 1,
    fontWeight: '700',
    width: '100%',
  },
  buttonsWrapper: {
    flexDirection: 'row',
    alignItems: 'stretch',
    width: '100%',
    alignContent: 'center',
    justifyContent: 'space-around',
  },
});
