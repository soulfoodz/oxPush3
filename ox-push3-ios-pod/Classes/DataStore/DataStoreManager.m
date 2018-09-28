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
#import "FDKeychain.h"
#import "KeychainWrapper.h"

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
    NSMutableArray* tokenArray;
    NSMutableArray* tokenArrayUserDef = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    if (tokenArrayUserDef != nil){
        tokenArray = [[NSMutableArray alloc] initWithArray: tokenArrayUserDef];
        [tokenArray insertObject:tokenEntity atIndex:0];
    } else {
        tokenArray = [[NSMutableArray alloc] initWithObjects:tokenEntity, nil];
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in tokenArray) {
        if ([tokenEntity isKindOfClass:[NSData class]]){
            [archiveArray addObject:tokenEntity];
        } else {
            NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
            [archiveArray addObject:personEncodedObject];
        }
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
    
    
    NSData *arrayData = [NSKeyedArchiver archivedDataWithRootObject:archiveArray];
    
    
    //Save token entities for KeyChain
//    KeychainWrapper* keyChain = [[KeychainWrapper alloc] init];
//    [keyChain setValue:arrayData forKey:@"token"];
    /*
    NSError *error = nil;
    
    [FDKeychain saveItem: archiveArray
                  forKey: KEY_ENTITIES
              forService: @"ox-push3"
           inAccessGroup: @"com.ios.gluu.com.1414degrees.moonunit"
       withAccessibility: FDKeychainAccessibleAfterFirstUnlock
                   error: &error];
    
    NSLog(@"ERROR - %@", error.description);
     */
}

- (BOOL)isUniqueTokenName:(NSString *)tokenName {
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray) {
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token.keyName isEqualToString:tokenName] == true) {
                return false;
            } else {
                continue;
            }
        }
    }
    
    return true;
}
    
-(NSArray*)getTokenEntitiesByID:(NSString*)keyID userName:(NSString*)userName{
    NSMutableArray* tokens = [[NSMutableArray alloc] init];
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = (TokenEntity*)[NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token isKindOfClass:[TokenEntity class]] && [token.ID isEqualToString:keyID] && [token.userName isEqualToString:userName]) {
                [tokens addObject:token];
            }
        }
    }
    
    return tokens;
}
    
-(NSArray*)getTokenEntities{
    NSMutableArray* tokens = [[NSMutableArray alloc] init];
    /*
    NSError *error = nil;
    
    NSArray* tokenArray = [FDKeychain itemForKey: KEY_ENTITIES
              forService: @"ox-push3"
           inAccessGroup: @"com.ios.gluu.com.1414degrees.moonunit"
                   error: &error];
    
    NSLog(@"ERROR - %@", error.description);
     */
    NSArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            [tokens addObject:token];
        }
    }
    return tokens;
}

-(void)setTokenEntitiesNameByID:(NSString*)keyID userName:(NSString*)userName newName:(NSString*)newName{
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    NSMutableArray* newTokenArray = [[NSMutableArray alloc] initWithArray:tokenArray];
    if (tokenArray != nil){
        NSUInteger index = 0;
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token.ID isEqualToString:keyID] && [token.userName isEqualToString:userName]) {
                TokenEntity* newToken = token;
                [newTokenArray removeObject:tokenData];
                newToken.keyName = newName;
                [newTokenArray insertObject:newToken atIndex:index];
                index++;
            }
        }
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in newTokenArray) {
        if ([tokenEntity isKindOfClass:[NSData class]]){
            [archiveArray addObject:tokenEntity];
        } else {
            NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
            [archiveArray addObject:personEncodedObject];
        }
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
    NSLog(@"Saved new TokenEntity name in database success");
}

-(TokenEntity*)getTokenEntityByKeyHandle:(NSString*)keyHandle{
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token.keyHandle isEqualToString:keyHandle]) {
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
        for (NSData* tokenData in [newTokenArray copy]){
            TokenEntity* token = (TokenEntity*)[NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token isKindOfClass:[TokenEntity class]] && [token.ID isEqualToString:tokenEntity.ID] && [token.userName isEqualToString:tokenEntity.userName]) {
                [newTokenArray removeObject:tokenData];
                NSString* count = tokenEntity.count;
                intCount = [count intValue];
                intCount += 1;
                tokenEntity.count = [NSString stringWithFormat:@"%d", intCount];
                [newTokenArray addObject:tokenEntity];
            }
        }
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in newTokenArray) {
        if ([tokenEntity isKindOfClass:[NSData class]]){
            [archiveArray addObject:tokenEntity];
        } else {
            NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
            [archiveArray addObject:personEncodedObject];
        }
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
    
    NSLog(@"Saved TokenEntity count to database success");
    return intCount;
}

-(BOOL)deleteTokenEntitiesByID:(NSString*)keyID userName:(NSString*) userName {
    NSMutableArray* tokenArray = [[NSUserDefaults standardUserDefaults] valueForKey:KEY_ENTITIES];
    NSMutableArray* newTokenArray = [[NSMutableArray alloc] initWithArray:tokenArray];
    if (tokenArray != nil){
        for (NSData* tokenData in tokenArray){
            TokenEntity* token = [NSKeyedUnarchiver unarchiveObjectWithData:tokenData];
            if ([token.ID isEqualToString:keyID] && [token.userName isEqualToString:userName]) {
                [newTokenArray removeObject:tokenData];
            }
        }
    }
    
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:tokenArray.count];
    for (TokenEntity *tokenEntity in newTokenArray) {
        if ([tokenEntity isKindOfClass:[NSData class]]){
            [archiveArray addObject:tokenEntity];
        } else {
            NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:tokenEntity];
            [archiveArray addObject:personEncodedObject];
        }
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:KEY_ENTITIES];
    return NO;
}

-(void)saveUserLoginInfo:(UserLoginInfo*)userLoginInfo{
    
    NSMutableArray* logs = [[NSUserDefaults standardUserDefaults] valueForKey:USER_INFO_ENTITIES];
    NSMutableArray* newlogs = [[NSMutableArray alloc] init];
    if (logs == nil){
        [newlogs addObject:userLoginInfo];
    } else {
        [newlogs addObject:userLoginInfo];
        for (NSData* logsData in logs){
            UserLoginInfo* info = (UserLoginInfo*)[NSKeyedUnarchiver unarchiveObjectWithData:logsData];
            [newlogs addObject:info];
        }
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
    if (logsDataArray != nil){
        for (NSData* logsData in logsDataArray){
            UserLoginInfo* info = (UserLoginInfo*)[NSKeyedUnarchiver unarchiveObjectWithData:logsData];
            [logs addObject:info];
        }
    }
    return logs;
}

-(void)deleteLogs:(NSArray*)logs{
    for (UserLoginInfo* info in logs){
        [[DataStoreManager sharedInstance] deleteLog:info];
    }
}

-(void)deleteLog:(UserLoginInfo*) log{
    NSMutableArray* logs = [[NSUserDefaults standardUserDefaults] valueForKey:USER_INFO_ENTITIES];
    NSMutableArray* newlogs = [[NSMutableArray alloc] init];
    for (NSData* logsData in logs){
        UserLoginInfo* info = (UserLoginInfo*)[NSKeyedUnarchiver unarchiveObjectWithData:logsData];
        if (![info.created isEqualToString:log.created]){
            [newlogs addObject:info];
        }
    }
    NSMutableArray *archiveArray = [NSMutableArray arrayWithCapacity:newlogs.count];
    for (UserLoginInfo *userLoginEntity in newlogs) {
        NSData *personEncodedObject = [NSKeyedArchiver archivedDataWithRootObject:userLoginEntity];
        [archiveArray addObject:personEncodedObject];
    }
    
    [[NSUserDefaults standardUserDefaults] setObject:archiveArray forKey:USER_INFO_ENTITIES];
}

-(BOOL)deleteAllLogs{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:USER_INFO_ENTITIES];
    return YES;
}

@end
