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
	// Do any additional setup after loading the view, typically from a nib.
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
