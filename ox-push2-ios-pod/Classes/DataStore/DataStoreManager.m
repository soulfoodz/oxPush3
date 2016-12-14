//
//  DataStoreManager.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "DataStoreManager.h"
#import <CoreData/CoreData.h>
#import "UserLoginInfo.h"

#define KEY_ENTITIES @"TokenEntities"
#define USER_INFO_ENTITIES @"LoginInfoEntities"

@implementation DataStoreManager{

}

+ (instancetype) sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

-(void)saveTokenEntity:(TokenEntity*)tokenEntity{
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    if (tokenArray != nil){
        [tokenArray addObject:tokenEntity];
    } else {
        tokenArray = [[NSMutableArray alloc] initWithObjects:tokenEntity, nil];
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in tokenArray) {
        NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
        [archiveArray addObject:personEncodedObject];
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
}
    
-(NSArray*)getTokenEntitiesByID:(NSString*)keyID{
    NSMutableArray* tokens = [[NSMutableArray alloc] init];
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = (TokenEntity*)[NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token isKindOfClass:[TokenEntity class]] && [token->ID isEqualToString:keyID]) {
                [tokens addObject:token];
            }
        }
    }
    
    return tokens;
}
    
-(NSArray*)getTokenEntities{
    NSMutableArray* tokens = [[NSMutableArray alloc] init];
    NSArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            [tokens addObject:token];
        }
    }
    return tokens;
}

-(void)setTokenEntitiesNameByID:(NSString*)keyID newName:(NSString*)newName{
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    NSMutableArray* newTokenArray = [[NSMutableArray alloc] initWithArray:tokenArray];
    int intCount = 0;
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token->ID isEqualToString:keyID]) {
                TokenEntity* newToken = token;
                [newTokenArray removeObject:tokenData];
                newToken->keyName = newName;
                [newTokenArray addObject:newToken];
            }
        }
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in newTokenArray) {
        NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
        [archiveArray addObject:personEncodedObject];
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
    NSLog(@"Saved new TokenEntity name in database success");
}

-(TokenEntity*)getTokenEntityByKeyHandle:(NSString*)keyHandle{
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    int intCount = 0;
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token->keyHandle isEqualToString:keyHandle]) {
                return token;
            }
        }
    }

    return nil;
}

-(int)incrementCountForToken:(TokenEntity*)tokenEntity{
    
    NSMutableArray* tokenArray = (NSMutableArray*)[[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    NSMutableArray* newTokenArray = [[NSMutableArray alloc] initWithArray:tokenArray];
    int intCount = 0;
    if (tokenArray != nil){
        for (NSData* tokenData in newTokenArray){
            TokenEntity* token = (TokenEntity*)[NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token isKindOfClass:[TokenEntity class]] && [token->ID isEqualToString:tokenEntity->ID]) {
                [newTokenArray removeObject:tokenData];
                NSString* count = tokenEntity->count;
                intCount = [count intValue];
                intCount += 1;
                tokenEntity->count = [NSString stringWithFormat:@"%d", intCount];
                [newTokenArray addObject:tokenEntity];
            }
        }
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in newTokenArray) {
        NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
        [archiveArray addObject:personEncodedObject];
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
    
    NSLog(@"Saved TokenEntity count to database success");
    return intCount;
}

-(BOOL)deleteTokenEntitiesByID:(NSString*)keyID{
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    NSMutableArray* newTokenArray = [[NSMutableArray alloc] initWithArray:tokenArray];
    int intCount = 0;
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token->ID isEqualToString:keyID]) {
                [newTokenArray removeObject:tokenData];
            }
        }
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in newTokenArray) {
        NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
        [archiveArray addObject:personEncodedObject];
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
    return NO;
}

-(void)saveUserLoginInfo:(UserLoginInfo*)userLoginInfo{

    NSMutableArray* logs = [[NSUserDefaults standardUserDefaults] valueForKey:USER_INFO_ENTITIES];
    NSMutableArray* newlogs = [[NSMutableArray alloc] initWithArray:logs];
    if (newlogs != nil){
        [newlogs addObject:userLoginInfo];
    } else {
        newlogs = [[NSMutableArray alloc] initWithObjects:userLoginInfo, nil];
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:newlogs.count];
    for (UserLoginInfo *userLoginEntity in newlogs) {
        NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:userLoginEntity];
        [archiveArray addObject:personEncodedObject];
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:USER_INFO_ENTITIES];

    NSLog(@"Saved UserLoginInfoEntity to database success");
}

-(NSArray*)getUserLoginInfo{
    NSMutableArray* logs = [[NSMutableArray alloc] init];
    NSMutableArray* logsDataArray = [[NSUserDefaults standardUserDefaults] valueForKey:USER_INFO_ENTITIES];
    if (logsDataArray == nil){
        for (NSData* logsData in logsDataArray){
            UserLoginInfo* info = [NSKeyedUnarchiver unarchiveObjectWithData:logsData];
            [logs addObject:info];
        }
    }
    return logs;
}

-(BOOL)deleteAllLogs{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:USER_INFO_ENTITIES];
    return YES;
}

@end
