//
//  gluuViewController.m
//  ox-push2-ios-pod
//
//  Created by NazarYavornytskyy on 08/19/2016.
//  Copyright (c) 2016 NazarYavornytskyy. All rights reserved.
//

#import "gluuViewController.h"
#import "OXPushManager.h"

@interface gluuViewController ()

@end

@implementation gluuViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    NSString* requestString = @"{\"app\":\"https://ce-release.gluu.org/identity/authentication/authcode\",\"username\":\"admin\",\"method\":\"enroll\",\"state\":\"35e37974-db3d-474b-ba4e-35fda6499ba9\",\"created\":\"2016-04-11T09:31:01.020000\",\"issuer\":\"https://ce-release.gluu.org\"}";
    NSData *data = [requestString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary* jsonDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    [self doRequest:jsonDictionary];
}

-(void)doRequest:(NSDictionary*)jsonDictionary{
    OXPushManager* oxPushManager = [[OXPushManager alloc] init];
    [oxPushManager setDevicePushToken:[[NSUserDefaults standardUserDefaults] stringForKey:@"deviceToken"]];
    [oxPushManager onOxPushApproveRequest:jsonDictionary isDecline:NO callback:^(NSDictionary *result,NSError *error){
        if (error) {
//            [self showAlertViewWithTitle:@"Info" andMessage:@"Authentication failed"];
            NSLog(@"Authentication failed");
        } else {
            //Success
//            [self showAlertViewWithTitle:@"Info" andMessage:@"Authentication success"];
            NSLog(@"Authentication success");
        }
    }];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
