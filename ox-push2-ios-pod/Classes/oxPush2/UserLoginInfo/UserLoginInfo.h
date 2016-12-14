//
//  UserLoginInfo.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/16/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, LogState) {
    LOGIN_SUCCESS = 0,
    LOGIN_FAILED,
    LOGIN_DECLINED,
    ENROLL_SUCCESS,
    ENROLL_FAILED,
    ENROLL_DECLINED,
    UNKNOWN_ERROR
};

//typedef enum LogState : NSString {
//    LOGIN_SUCCESS,
//    LOGIN_FAILED,
//    LOGIN_DECLINED,
//    ENROLL_SUCCESS,
//    ENROLL_FAILED,
//    NROLL_DECLINED,
//    UNKNOWN_ERROR
//} LogState;

@interface UserLoginInfo : NSObject{
    @public
    NSString* userName;
    NSString* created;
    NSString* application;
    NSString* issuer;
    NSString* authenticationType;
    NSString* authenticationMode;
    NSString* locationIP;
    NSString* locationCity;
    LogState logState;
    NSString* errorMessage;
    
}
    
+ (instancetype) sharedInstance;
    
- (NSString *)displayNameForWonderfulType:(LogState)logState;

@end
