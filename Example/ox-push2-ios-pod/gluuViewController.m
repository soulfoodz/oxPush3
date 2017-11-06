//
//  gluuViewController.m
//  ox-push2-ios-pod
//
//  Created by NazarYavornytskyy on 08/19/2016.
//  Copyright (c) 2016 NazarYavornytskyy. All rights reserved.
//

#import "gluuViewController.h"
#import "OXPushManager.h"
#import "TokenEntity.h"
#import "SCLAlertView.h"
#import "DataStoreManager.h"
//#import "ox-push3-swift.h"
#import "ox-push3_Example-Bridging-Header.h"

@interface gluuViewController (){

    NSString* alertMessage;
//    PeripheralScanner* scanner;
    BOOL isSecureClick;
}

    @end

@implementation gluuViewController
    
- (void)viewDidLoad
    {
        [super viewDidLoad];
        
//        NSString* requestString = @"{\"app\":\"https://ce-release.gluu.org/identity/authentication/authcode\",\"username\":\"admin\",\"method\":\"enroll\",\"state\":\"35e37974-db3d-474b-ba4e-35fda6499ba9\",\"created\":\"2016-04-11T09:31:01.020000\",\"issuer\":\"https://ce-release.gluu.org\"}";
//        NSData *data = [requestString dataUsingEncoding:NSUTF8StringEncoding];
//        NSDictionary* jsonDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
//        [self doRequest:jsonDictionary];
        isSecureClick = YES;
    }
    
- (IBAction)scanAction:(id)sender
    {
        //    [self loadApproveDenyView:nil];
        [self initQRScanner];
        if ([QRCodeReader isAvailable]){
//            [self updateStatus:NSLocalizedString(@"QRCodeScanning", @"QR Code Scanning")];
            [self presentViewController:qrScanerVC animated:YES completion:NULL];
        } else {
            [self showAlertViewWithTitle:NSLocalizedString(@"AlertTitle", @"Info") andMessage:NSLocalizedString(@"AlertMessageNoQRScanning", @"No QR Scanning available")];
        }
    }
    
-(void)doRequest:(NSDictionary*)jsonDictionary{
    OXPushManager* oxPushManager = [[OXPushManager alloc] init];
    [oxPushManager setDevicePushToken:[[NSUserDefaults standardUserDefaults] stringForKey:@"deviceToken"]];
    [oxPushManager onOxPushApproveRequest:jsonDictionary isDecline:NO isSecureClick:isSecureClick callback:^(NSDictionary *result,NSError *error){
        if (error) {
            [self showAlertViewWithTitle:@"Info" andMessage:[NSString stringWithFormat:@"%@ failed", alertMessage]];
            NSLog(@"%@", [NSString stringWithFormat:@"%@ failed", alertMessage]);
        } else {
            //Success
            [self showAlertViewWithTitle:@"Info" andMessage:[NSString stringWithFormat:@"%@ success", alertMessage]];
            NSLog(@"%@", [NSString stringWithFormat:@"%@ success", alertMessage]);
        }
    }];
}

-(void)initBLE:(NSData*)valueData{
    //Commented out at the moment - no need BLE u2f implementation right now
//    scanner = [[PeripheralScanner alloc] init];
//    scanner.valueForWrite = valueData;
//    [scanner start];
}

-(void)initQRScanner{
    // Create the reader object
    QRCodeReader *reader = [QRCodeReader readerWithMetadataObjectTypes:@[AVMetadataObjectTypeQRCode]];
    
    // Instantiate the view controller
    qrScanerVC = [QRCodeReaderViewController readerWithCancelButtonTitle:@"Cancel" codeReader:reader startScanningAtLoad:YES showSwitchCameraButton:YES showTorchButton:YES];
    
    // Set the presentation style
    qrScanerVC.modalPresentationStyle = UIModalPresentationFormSheet;
    
    // Define the delegate receiver
    qrScanerVC.delegate = self;
    
    // Or use blocks
    [reader setCompletionWithBlock:^(NSString *resultAsString) {
        if(resultAsString && !isResultFromScan){
            NSLog(@"%@", resultAsString);
            [qrScanerVC dismissViewControllerAnimated:YES completion:^(void){
                NSData *data = [resultAsString dataUsingEncoding:NSUTF8StringEncoding];
                NSDictionary* jsonDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
                BOOL isEnroll = [[DataStoreManager sharedInstance] getTokenEntities] == 0;
                alertMessage = isEnroll ? @"Enroll" : @"Authentication";
                [self doRequest:jsonDictionary];
                isResultFromScan = YES;
            }];
        }
    }];
    isResultFromScan = NO;
}

-(void)showAlertViewWithTitle:(NSString*)title andMessage:(NSString*)message{
    SCLAlertView *alert = [[SCLAlertView alloc] initWithNewWindow];
    [alert addButton:@"OK" actionBlock:^(void) {
        NSLog(@"OK clicked");
        //            [self loadViewByID:@"main_view_seque"];
    }];
    [alert showCustom:nil color:[UIColor blueColor] title:title subTitle:message closeButtonTitle:nil duration:0.0f];
}

-(void)showProcessingAlertViewWithessage:(NSString*)message{
    SCLAlertView *alertProcessing = [[SCLAlertView alloc] initWithNewWindow];
    [alertProcessing showWaiting:nil subTitle:message closeButtonTitle:nil duration:3.0f];
}
    
- (void)didReceiveMemoryWarning
    {
        [super didReceiveMemoryWarning];
        // Dispose of any resources that can be recreated.
    }
    
    @end
