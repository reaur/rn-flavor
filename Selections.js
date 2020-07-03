import React, { Component } from 'react';
import { SectionList, StyleSheet, Text, View, NativeModules } from 'react-native';

export default class SectionListBasics extends Component {
   onPressList(productName) {
    alert(productName);
  }

    buyItem(productName) {
    NativeModules.HMSIap.buyProduct(productName).then((status) => {
            alert(status);
          }, (code, message) => {
           alert(message);
       });
  }


  render() {
    return (
      <View style={styles.container}>
        <SectionList
          sections={[
            {title: 'Products', data:  ['Jackson', 'James', 'Jillian', 'Jimmy', 'Joel', 'John', 'Julie']}
          ]}
          renderItem={({item}) => <Text style={styles.item} onPress={() => this.buyItem(item)}>{item}</Text>}
          renderSectionHeader={({section}) => <Text style={styles.sectionHeader}>{section.title}</Text>}
          keyExtractor={(item, index) => index}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
   flex: 1,
   paddingTop: 22
  },
  sectionHeader: {
    paddingTop: 2,
    paddingLeft: 10,
    paddingRight: 10,
    paddingBottom: 2,
    fontSize: 14,
    fontWeight: 'bold',
    backgroundColor: 'rgba(247,247,247,1.0)',
  },
  item: {
    padding: 10,
    fontSize: 18,
    height: 44,
  },
})
