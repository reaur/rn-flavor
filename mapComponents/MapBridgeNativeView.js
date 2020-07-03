//  Created by react-native-create-bridge

import React, {Component, useEffect} from 'react';
import {requireNativeComponent} from 'react-native';
import PropTypes from 'prop-types';

//module.exports = requireNativeComponent('MapBridge');

export default class MapBridge extends Component {
  /**
   * this method will receive the event sent from the native (java) side
   */
  onChange = event => {
    console.log(
      'map',
      'mapBridge component onChange event:',
      event.nativeEvent.eventName,
    );

    switch (event.nativeEvent.eventName) {
      case 'MAP_READY': //this event is received when map is ready to draw markers and register click events
        if (this.props.onMapReady) this.props.onMapReady();
        break;
      case 'MARKER_CLICKED': //this event is received when a marker is clicked
        const marker = {
          id: event.nativeEvent.id,
          lat: event.nativeEvent.lat,
          lng: event.nativeEvent.lng,
          title: event.nativeEvent.title,
          snippet: event.nativeEvent.snippet,
        };
        if (this.props.onMarkerTap) this.props.onMarkerTap(marker);
        break;
      case 'MAP_LONG_PRESS': //this event is received when the user long press the map
        const coords = {
          lat: event.nativeEvent.lat,
          lng: event.nativeEvent.lng,
        };
        console.log('Map long pressed!', coords);
        if (this.props.onLongPress) this.props.onLongPress(coords);
        break;
    }
  };

  render() {
    return <MapBridgeView {...this.props} onChange={this.onChange} />;
  }
}

MapBridge.propTypes = {
  onMarkerTap: PropTypes.func,
  markers: PropTypes.array,
  selectedMarker: PropTypes.object,
  showCurrentLocation: PropTypes.bool,
  isReady: PropTypes.bool,
  markerIcon : PropTypes.string
};

const MapBridgeView = requireNativeComponent('MapBridge', MapBridgeView, {
  nativeOnly: {
    onChange: true,
  },
});
