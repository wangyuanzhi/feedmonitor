cls
call mvn package
call vmc --path target\appassembler update
call vmc files feedmonitor-batch logs/stdout.log
