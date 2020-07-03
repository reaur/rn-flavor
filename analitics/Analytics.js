import React from 'react';
import { View, Button, NativeModules } from 'react-native';


const Analytics = () => {

    const logEvent = (name, bundle) => {
        NativeModules.HMSAnalytics.logEvent(name, JSON.stringify(bundle));
    }

    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Button title="Send Events"  onPress={() => logEvent("test", { message: "hi" })} />
        </View>
    );
};

export default Analytics;
