namespace java org.opencron.common.job

enum Action {
    PING,
    EXECUTE,
    PASSWORD,
    KILL,
    MONITOR,
    PROXY
}

struct Request {
     1:string hostName,
     2:i32 port,
     3:Action action,
     4:string password,
     5:map<string, string> params
}

struct Response {
     1:Action action,
     2:map<string, string> result,
     3:i32 exitCode,
     4:bool success,
     5:i64 startTime,
     6:i64 endTime,
     7:string message
}

service Opencron {
 Response ping(1:Request request),
 Response execute(1:Request request),
 Response password(1:Request request),
 Response kill(1:Request request),
 Response monitor(1:Request request)
 Response proxy(1:Request request)
}