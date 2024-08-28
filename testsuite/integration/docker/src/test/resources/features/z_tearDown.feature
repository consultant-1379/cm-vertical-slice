@Teardown
Feature: z_Teardown for the tests.

  Scenario Outline: Unsupervise and Delete a node from DPS.
    Given Node <nodeName> is deleted
    Then Node <nodeName> will not exist in DPS

    Examples:
      | nodeName       |
      | LTE02ERBS00001 |

  Scenario Outline: Deleting <moFdn> on Netsim
    When Execute DELETE_MO netsim command:
      | fdn     |
      | <moFdn> |

    Examples:
      | moFdn                                                                       |
      | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Ireland   |
      | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Westmeath |
      | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir   |
      | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir1  |

  Scenario Outline: Resetting <moFdn> attribute <netsimAttributeKeysValues> on Netsim
    When Execute SET_MO_ATTRIBUTES netsim command:
      | fdn     | attributeKeysValues         |
      | <moFdn> | <netsimAttributeKeysValues> |

    Examples:
      | moFdn                                                                                                   | netsimAttributeKeysValues      |
      | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | userLabel=default              |
      | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | ulSrsEnable=true               |
      | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | zzzTemporary13=0               |
      | MeContext=LTE02ERBS00001,ManagedElement=1,Equipment=1,AntennaUnitGroup=1,AntennaNearUnit=1,TmaSubUnit=1 | iuantAntennaOperatingBand=1111 |
      | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | administrativeState=1]         |
      | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | tac=0                          |
