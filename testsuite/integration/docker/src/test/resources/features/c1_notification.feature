@Notification @RunAllTests
Feature: c1_Notification Processing capabilities - Notification Types.

  Scenario Outline: Verify a CREATE_MO notifications on a node with default attributes are persisted in DPS.
    Given The node <nodeName> is SYNCHRONIZED
    When Execute CREATE_MO netsim command:
      | fdn                                                          | moType | moName  |
      | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1 | Sctp   | Mjolnir |
    Then DPS will contain the MO <moFdn>:
      |  |  |
    When I read the netsim topology for node <nodeName>
    When I read the dps topology for node <nodeName>
    Then the topologies for node <nodeName> should match
    When Execute DELETE_MO netsim command:
      | fdn     |
      | <moFdn> |
    Then The node will not contain the managed object <moFdn>:

    Examples:
      | nodeName       | moFdn                                                                     |
      | LTE02ERBS00001 | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir |

  Scenario Outline: Verify a CREATE_MO notification on a node with non default attributes are persisted in DPS.
    Given The node <nodeName> is SYNCHRONIZED
    When Execute CREATE_MO netsim command:
      |fdn|moType|moName|attributeKeysValues|
      |MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Mjolnir|userLabel=abcd|
    When I wait 5 seconds
    Then VERIFY_MO_PRESENCE on netsim:
      |fdn|moType|moName|attributeKeysValues|
      |MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Mjolnir|userLabel=abcd|
    Then DPS will contain <moFdn> with attribute name <dpsKey> and object value <dpsValue>
    When I read the netsim topology for node <nodeName>
    When I read the dps topology for node <nodeName>
    Then the topologies for node <nodeName> should match
    When Execute DELETE_MO netsim command:
      |fdn|
      |<moFdn>|
    Then The node will not contain the managed object <moFdn>:

    Examples:
      | nodeName       | moFdn | dpsKey | dpsValue |
      | LTE02ERBS00001 | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir | userLabel | abcd |

  Scenario Outline: Verify a DELETE_MO notification on a node are persisted in DPS.
    Given The node <nodeName> is SYNCHRONIZED
    When Execute CREATE_MO netsim command:
      |fdn|moType|moName|
      |MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Mjolnir|
    Then DPS will contain the MO <moFdn>:
      | | |
    When Execute DELETE_MO netsim command:
      |fdn|
      |<moFdn>|
    When I wait 5 seconds
    Then ManagedObject <moFdn> will not exist in DPS

    Examples:
      | nodeName       | moFdn |
      | LTE02ERBS00001 | MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir |

  Scenario Outline: Verify a basic <avcType> avc notification gets persisted in DPS.
    Given The node <nodeName> is SYNCHRONIZED
    When Execute SET_MO_ATTRIBUTES netsim command:
      | fdn     | attributeKeysValues         |
      | <moFdn> | <netsimAttributeKeysValues> |
    When I wait 10 seconds
    Then DPS will contain <moFdn> with attribute name <dpsKey> and object value <dpsValue>

    Examples:
      | nodeName       | avcType | moFdn                                                                                                   | netsimAttributeKeysValues      | dpsKey                    | dpsValue |
      | LTE02ERBS00001 | STRING  | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | userLabel=mjolnir              | userLabel                 | mjolnir  |
      | LTE02ERBS00001 | BOOLEAN | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | ulSrsEnable=false              | ulSrsEnable               | false    |
      | LTE02ERBS00001 | INTEGER | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | zzzTemporary13=2               | zzzTemporary13            |        2 |
      | LTE02ERBS00001 | LONG    | MeContext=LTE02ERBS00001,ManagedElement=1,Equipment=1,AntennaUnitGroup=1,AntennaNearUnit=1,TmaSubUnit=1 | iuantAntennaOperatingBand=1234 | iuantAntennaOperatingBand |     1234 |
      | LTE02ERBS00001 | ENUM    | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1               | administrativeState=2]         | administrativeState       | LOCKED   |
