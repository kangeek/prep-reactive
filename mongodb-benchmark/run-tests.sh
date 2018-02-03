#! /bin/bash

logdir="target/logs"
mongohost="ec2-54-237-58-164.compute-1.amazonaws.com:27017"

# export JAVA_HOME=

# Command to remove the previous run data but leave empty data files.
#compact="db.usertable.remove();printjson(db.runCommand({'compact':'usertable','force':true}));"
compact="db.usertable.drop();"

mkdir -p ${logdir}

threadConnMap[1]="1 2 5 10 20 25"
threadConnMap[2]="2 5 10 15 20 25 50"
threadConnMap[10]="10 15 20 30 40 50 75 100 125"

for workload in workloada workloadb workloadc workloadd workloade workloadf ; do
  for connections in 1 2 10 ; do
    for threads in ${threadConnMap[${connections}]} ; do
      for driver in mongodb mongodb-async; do
        #
        # Remove all documents and compact (avoid re-disk allocation)."
        #
        mongo --eval "${compact}" ${mongohost}/ycsb >> /dev/null 2>&1 
      
        #
        # Load the data set.
        #
        now=$( date --utc "+%F %H:%M:%S" )
        printf "%s: Load: %9s with %-13s thread=%3s conns=%2s" "${now}" ${workload} ${driver} ${threads} ${connections}
      
        let start=$( date --utc "+%s" )
        ./bin/ycsb load ${driver} -s -P workloads/${workload} -threads ${threads}            \
                 -p "mongodb.url=mongodb://${mongohost}/ycsb?w=1&maxPoolSize=${connections}&waitQueueMultiple=${threads}" \
                 >  ${logdir}/${workload}-${driver}-threads_${threads}-conns_${connections}-load.out \
                 2> ${logdir}/${workload}-${driver}-threads_${threads}-conns_${connections}-load.err
        let end=$( date --utc "+%s" )
        let delta=end-start
        printf " --> %10d seconds\n" ${delta}           
      
        #
        # Run the workload.
        #
        now=$( date --utc "+%F %H:%M:%S" )
        printf "%s:  Run: %9s with %-13s thread=%3s conns=%2s" "${now}" ${workload} ${driver} ${threads} ${connections}
      
        let start=$( date --utc "+%s" )
        ./bin/ycsb run ${driver} -s -P workloads/${workload} -threads ${threads}             \
                 -p "mongodb.url=mongodb://${mongohost}/ycsb?w=1&maxPoolSize=${connections}&waitQueueMultiple=${threads}" \
                  >  ${logdir}/${workload}-${driver}-threads_${threads}-conns_${connections}-run.out \
                  2> ${logdir}/${workload}-${driver}-threads_${threads}-conns_${connections}-run.err                  
        let end=$( date --utc "+%s" )
        let delta=end-start
        printf " --> %10d seconds\n" ${delta}           
      done
    done
  done
done
