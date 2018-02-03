#!/bin/bash

RECORD_COUNT=30000
OPERATION_COUNT=60000
MONGO_HOST=localhost:27017
DROP_TABLE_CMD="db.usertable.drop();"
MAX_POOL_SIZE=32
#   -p "mongodb.url=mongodb://${MONGO_HOST}/ycsb?w=1&maxPoolSize=${MAX_POOL_SIZE}&waitQueueMultiple=${3}" \

function benchmark() {
  mongo --eval "${DROP_TABLE_CMD}" ${MONGO_HOST}/ycsb >> /dev/null 2>&1
  echo "=================[$(date '+%F %T')] Begins benchmark : ${1} - workload${2} - ${3} threads==================" | tee -a output/mongostat.log
# echo "[$(date '+%F %T')] Begins benchmark : ${1} - workload${2} - ${3} threads" | tee -a output/mongostat.log

  ### load
  bin/ycsb load ${1} -P workloads/workload${2} -s \
    -p "mongodb.url=mongodb://${MONGO_HOST}/ycsb?w=1&maxPoolSize=${MAX_POOL_SIZE}&waitQueueMultiple=${3}" \
    -p recordcount=${RECORD_COUNT} -p operationcount=${OPERATION_COUNT} -threads ${3} \
    > output/workload${2}-${3}-load-${1}.txt \
    2> output/tmp-load.txt
  echo $(printf "[%s - workload%s: thread=%3s,maxconn=%2s] " ${1} ${2} ${3} ${MAX_POOL_SIZE}) \
    $(grep "${RECORD_COUNT} operations" output/tmp-load.txt) \
    | tee -a output/mongo-benchmark.log

  ### run
  bin/ycsb run ${1} -P workloads/workload${2} -s \
    -p "mongodb.url=mongodb://${MONGO_HOST}/ycsb?w=1&maxPoolSize=${MAX_POOL_SIZE}&waitQueueMultiple=${3}" \
    -p recordcount=${RECORD_COUNT} -p operationcount=${OPERATION_COUNT} -threads ${3} \
    > output/workload${2}-${3}-run-${1}.txt \
    2> output/tmp-run.txt
  echo $(printf "[%s - workload%s: thread=%3s,maxconn=%2s] " ${1} ${2} ${3} ${MAX_POOL_SIZE}) \
    $(grep "${OPERATION_COUNT} operations" output/tmp-run.txt) \
    | tee -a output/mongo-benchmark.log

# echo "[$(date '+%F %T')] Ends benchmark : mongodb - workload${2} - ${3} threads"
}

function sync_benchmark() {
  benchmark mongodb a 1
  benchmark mongodb a 5
  benchmark mongodb a 10
  benchmark mongodb a 20
  benchmark mongodb a 30
  benchmark mongodb a 40
  benchmark mongodb a 50
  benchmark mongodb a 60
}
function async_benchmark() {
  benchmark mongodb-async a 1
  benchmark mongodb-async a 5
  benchmark mongodb-async a 10
  benchmark mongodb-async a 20
  benchmark mongodb-async a 30
  benchmark mongodb-async a 40
  benchmark mongodb-async a 50
  benchmark mongodb-async a 60
  benchmark mongodb-async a 70
  benchmark mongodb-async a 80
}

echo "=================================================================================================================" >> output/mongo-benchmark.log
echo "$(date '+%F %T')" >> output/mongo-benchmark.log

sync_benchmark
async_benchmark
