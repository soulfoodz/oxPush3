//
//  gluuViewController.h
//  ox-push2-ios-pod
//
//  Created by NazarYavornytskyy on 08/19/2016.
//  Copyright (c) 2016 NazarYavornytskyy. All rights reserved.
//

@import UIKit;

#import "QRCodeReaderViewController.h"
#import "QRCodeReaderDelegate.h"

@interface gluuViewController : UIViewController <QRCodeReaderDelegate> {

    QRCodeReaderViewController *qrScanerVC;
    
    BOOL isResultFromScan;
}

@end
