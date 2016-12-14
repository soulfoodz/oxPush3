//
//  UserLoginInfo.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/16/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "UserLoginInfo.h"

@implementation UserLoginInfo

+ (instancetype) sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}
    
- (NSString *)displayNameForWonderfulType:(LogState)logState {
    switch(logState) {
        case LOGIN_SUCCESS:
            return @"LOGIN_SUCCESS";
        case LOGIN_FAILED:
            return @"LOGIN_FAILED";
        case LOGIN_DECLINED:
            return @"LOGIN_DECLINED";
        case ENROLL_SUCCESS:
            return @"ENROLL_SUCCESS";
        case ENROLL_FAILED:
            return @"ENROLL_FAILED";
        case ENROLL_DECLINED:
            return @"ENROLL_DECLINED";
        case UNKNOWN_ERROR:
            return @"UNKNOWN_ERROR";
    }
    [NSException raise:NSInvalidArgumentException format:@"The given format type number, %ld, is not known.", logState];
    return nil; // Keep the compiler happy - does not understand above line never returns!
}
    
    /* This code has been added to support encoding and decoding my objecst */
    
-(void)encodeWithCoder:(NSCoder *)encoder
    {
        //Encode the properties of the object
        [encoder encodeObject:userName forKey:@"userName"];
        [encoder encodeObject:created forKey:@"created"];
        [encoder encodeObject:application forKey:@"application"];
        [encoder encodeObject:issuer forKey:@"issuer"];
        [encoder encodeObject:authenticationType forKey:@"authenticationType"];
        [encoder encodeObject:authenticationMode forKey:@"authenticationMode"];
        [encoder encodeObject:locationIP forKey:@"locationIP"];
        [encoder encodeObject:locationCity forKey:@"locationCity"];
        [encoder encodeObject:[self displayNameForWonderfulType:logState] forKey:@"logState"];
        [encoder encodeObject:errorMessage forKey:@"errorMessage"];
    }
    
-(id)initWithCoder:(NSCoder *)decoder
    {
        self = [super init];
        if ( self != nil )
        {
            //decode the properties
            userName = [decoder decodeObjectForKey:@"userName"];
            created = [decoder decodeObjectForKey:@"created"];
            application = [decoder decodeObjectForKey:@"application"];
            issuer = [decoder decodeObjectForKey:@"issuer"];
            authenticationType = [decoder decodeObjectForKey:@"authenticationType"];
            authenticationMode = [decoder decodeObjectForKey:@"authenticationMode"];
            locationIP = [decoder decodeObjectForKey:@"locationIP"];
            locationCity = [decoder decodeObjectForKey:@"locationCity"];
            locationCity = [decoder decodeObjectForKey:@"locationCity"];
            logState = [decoder decodeObjectForKey:@"logState"];
            errorMessage = [decoder decodeObjectForKey:@"errorMessage"];
        }
        return self;
    }


@end
