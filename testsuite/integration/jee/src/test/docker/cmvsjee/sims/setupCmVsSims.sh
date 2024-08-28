#!/usr/bin/env bash
set -o errexit # exit when a command fails.
set -o nounset # exit when this script tries to use undeclared variables
set -o pipefail # to catch pipe errors

#
# Main
#
SCRIPTNAME=$(basename "$0")
echo "RUNNING: $SCRIPTNAME $*"

echo "INFO: Setup vertical slice sims started on `date`"
mml="/tmp/$SCRIPTNAME.mml"

#
# Delete all existing zipped simulations
#
echo "INFO: Deleting all compressed sims"
rm -rfv /netsim/netsimdir/*.zip

#
# Delete all existing uncompressed simulations
#
echo "INFO: Deleting all uncompressed sims"
echo "#`date`" > $mml
sims=$(cd /netsim/netsimdir/ && echo [[:upper:]]*[!.zip] | xargs -n 1 | perl -lne 'print if!/^Re|^Se|up/')

for sim in $sims
do
  echo "INFO: Deleting sim: $sim"
  echo -e ".deletesimulation $sim force" >> $mml
done
su netsim -c "cat $mml | /netsim/inst/netsim_pipe"

#
# Import all vertical slice simulations
#
echo "INFO: Copying vertical slice sims"
cp -fv /netsim/sims/*.zip /netsim/netsimdir/

echo "INFO: Installing vertical slice sims"
echo "#`date`" > $mml
sims=`ls /netsim/netsimdir/ | (grep -i zip || true)`

i=1; sum=`echo $sims | xargs -n 1 | sed '/^$/d' | wc -l`
for sim in $sims
do
  echo "INFO: ($i/$sum) Opening sim: $sim"
  echo -e ".uncompressandopen $sim force" >> $mml
  sim=${sim%.zip}

  NODE_TYPES="lte|mgw|spitfire|tcu|rnc|esapc|epg|mtas|cscf|sbg|vmrf|vbgf|ipworks|hss-fe"
  if [[ `echo $sim | (egrep -ci "${NODE_TYPES}" || true)` -gt 0 ]]
  then
    echo "INFO: ($i/$sum) Unsetting tmpfs for sim: $sim"
    echo -e ".open $sim" >> $mml
    echo -e ".select network" >> $mml
    echo -e ".set fs tmpfs off" >> $mml
    echo -e ".set save" >> $mml
  else
    echo "INFO: ($i/$sum) No tmpfs update needed for sim: $sim"
  fi
  echo -e ".save" >> $mml

  i=$((i + 1))
done
su netsim -c "cat $mml | /netsim/inst/netsim_pipe"

#
# Starting nodes
#
startSims() {
  for sim in "${@:2}"
  do
    sudo -u netsim perl "/netsim/docker/startSims.pl" -nomsg -regExp "$sim" -numOfNes $1
  done
}

echo "INFO: Starting vertical slice sims"
startSims 1 LTEG1301 LTEG1132 RNCV71543 SGSN-15B
startSims 5 LTEH140

#
# End
#
echo "INFO: Setup vertical slice sims finished on `date`"
