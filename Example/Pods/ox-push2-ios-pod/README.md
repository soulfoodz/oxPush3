# ox-push2-ios-pod

## How to install

To use ox-push2 framework you can edit your pod file, just add next:

      pod 'ox-push2-ios-pod', :git => 'https://github.com/GluuFederation/ox-push2-ios-pod.git'

Or copy&past folder "ox-push2-ios-pod" to your project folder


## How to use

  First you need to create object of OXPushManager class:
  
      OXPushManager* oxPushManager = [[OXPushManager alloc] init];

  Also recomended to set device's token for Push Notifications. During registration device's UDID on Gluu's server app sends token for Push Notification and after that when you try to do authentication request you will get Push to your device with authentication request data. 
  To set token just use method "setDevicePushToken":
  
      [oxPushManager setDevicePushToken:@"deviceToken"];

  To send enroll/authentication request you should call method "onOxPushApproveRequest":
  
      [oxPushManager onOxPushApproveRequest:jsonDictionary isDecline:NO callback:^(NSDictionary *result,NSError *error){
        if (error) {
            [self showAlertViewWithTitle:@"Info" andMessage:@"Authentication failed"];
        } else {
            //Success
            [self showAlertViewWithTitle:@"Info" andMessage:@"Authentication success"];
        }
    }];
  
  Parameters: 
  - jsonDictionary: dictionary with request data
  - isDecline: boolean parameter to inform oxPush2 lib if you Approve request or not
  - callback: simple callback method returns result and error
  

