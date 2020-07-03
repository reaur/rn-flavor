import React, { Component } from 'react';
import { Button, StyleSheet, View, NativeModules } from 'react-native';
import BuildConfig from 'react-native-build-config'


export default class ButtonBasics extends Component {

  
  _onPressButton() {
  //  var bundle = { message: "hi" };
  //  NativeModules.HMSAnalytics.logEvent("Heeey", JSON.stringify(bundle));
    alert(BuildConfig.FLAVOR);
 // NativeModules.HMSIap.getProducts();
  }


  render() {
    return (
      <View style={styles.container}>
        <View style={styles.buttonContainer}>
          <Button
            onPress={this._onPressButton}
            title="Press Me"
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            onPress={this._onPressButton}
            title="Press Me"
            color="#841584"
          />
        </View>
        <View style={styles.alternativeLayoutButtonContainer}>
          <Button
            onPress={this._onPressButton}
            title="This looks great!"
          />
          <Button
            onPress={this._onPressButton}
            title="OK!"
            color="#841584"
          />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
   flex: 1,
   justifyContent: 'center',
  },
  buttonContainer: {
    margin: 20
  },
  alternativeLayoutButtonContainer: {
    margin: 20,
    flexDirection: 'row',
    justifyContent: 'space-between'
  }
});
