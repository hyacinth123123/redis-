local key=KEYS[1];
local threadId=ARGV[1];
local releaseTime=ARGV[2];
if(redis.call('HEXISTS',KEY,threadId)==0)then
    return nil;
end
local count=redis.call('HINCRBY',KEY,threadId,-1);
if(count>0)then
    redis.call('EXPIRE',key,releaseTime);
    return nil;
else
    redis.call('DEL',key);
    return nil;

end