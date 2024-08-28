@Notification @RunAllTests
Feature: c2_Notification Processing capabilities - General.

  Scenario Outline: Verify that no notification gets processed and there is no valid subscription after supervision is turned off.
    Given The node <nodeName> is UNSYNCHRONIZED
    When Execute CREATE_MO netsim command:
      |fdn|moType|moName|
      |MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Mjolnir1|
    When I wait 5 seconds
    Then ManagedObject <moFdn> will not exist in DPS
    Then The Generation Counter in dps and netsim for node <nodeName> should not be equal
    Then Validate that the node <nodeName> has no subscription
    When The node <nodeName> is SYNCHRONIZED
    Then Validate that the node <nodeName> has a valid subscription
    Then DPS will contain the MO <moFdn>:
      | | |
    Then The Generation Counter in dps and netsim for node <nodeName> should be equal
    When Execute DELETE_MO netsim command:
      |fdn|
      |<moFdn>|

    Examples:
      | nodeName       |moFdn|
      | LTE02ERBS00001 |MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir1|

  @ResetPibForEviction
  Scenario Outline: Verify the processing when an expected notification gets evicted.
    Given The node <nodeName> is SYNCHRONIZED
    Given The cm_notification_buffer_eviction_time_value_milliseconds PIB parameter is updated to 5000
    When A pause operation is invoked on jms-queue name NetworkElementNotifications
    When I wait 5 seconds
    When Execute SET_MO_ATTRIBUTES netsim command:
      | fdn     | attributeKeysValues   |
      | <moFdn> | <attribute1>=<value1> |
    When I wait 5 seconds
    Then DPS will not contain <moFdn> with attribute name <attribute1> and object value <value1>
    When A remove-messages operation is invoked on jms-queue name NetworkElementNotifications
    When A resume operation is invoked on jms-queue name NetworkElementNotifications
    Then The Generation Counter in dps and netsim for node <nodeName> should not be equal
    When Execute SET_MO_ATTRIBUTES netsim command:
      | fdn     | attributeKeysValues   |
      | <moFdn> | <attribute2>=<value2> |
    When I wait 10 seconds
    Then The Generation Counter in dps and netsim for node <nodeName> should be equal
    Then DPS will contain <moFdn> with attribute name <attribute1> and object value <value1>
    Then DPS will contain <moFdn> with attribute name <attribute2> and object value <value2>

    Examples:
      | nodeName       | avcType | moFdn                                                                                    |attribute1|value1|attribute2|value2 |
      | LTE02ERBS00001 | STRING  | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1|tac       |112   |userLabel |athlone|

  Scenario Outline: Process notification for one level SubNetwork
    Given Node <nodeName> is deleted
    Then Node <nodeName> will not exist in DPS
    When I wait 10 seconds
    When I create a node called <nodeName>:
      | ossPrefix |<subNetworkFdn>|
    Then The node <nodeName> is SYNCHRONIZED

    When Execute CREATE_MO netsim command:
      |fdn|moType|moName|
      |SubNetwork=Ireland,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Westmeath|
    Then DPS will contain the MO <moFdn>:
      | | |
    When I read the netsim topology for node <nodeName>
    When I read the dps topology for node <nodeName>
    Then the topologies for node <nodeName> should match
    When Execute DELETE_MO netsim command:
      |fdn|
      |<moFdn>|
    Then The node will not contain the managed object <moFdn>:

    When Node <nodeName> is deleted
    When I wait 10 seconds
    When ManagedObject <subNetworkFdn> is deleted
    Then ManagedObject <subNetworkFdn> will not exist in DPS
    Then Node <nodeName> will not exist in DPS
    When I wait 10 seconds
    When I create a node called <nodeName>:
      | | |
    Then The node <nodeName> is SYNCHRONIZED

    Examples:
      | nodeName       | subNetworkFdn |moFdn|
      | LTE02ERBS00001 | SubNetwork=Westmeath | SubNetwork=Westmeath,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Westmeath|

  Scenario Outline: Process notification for two level SubNetwork
    Given Node <nodeName> is deleted
    Then Node <nodeName> will not exist in DPS
    When I wait 10 seconds
    When I create a node called <nodeName>:
      | ossPrefix |<subNetworkFdn>|
    Then The node <nodeName> is SYNCHRONIZED

    When Execute CREATE_MO netsim command:
      |fdn|moType|moName|
      |SubNetwork=Ireland,SubNetwork=Westmeath,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Ireland|
    Then DPS will contain the MO <moFdn>:
      | | |
    When I read the netsim topology for node <nodeName>
    When I read the dps topology for node <nodeName>
    Then the topologies for node <nodeName> should match
    When Execute DELETE_MO netsim command:
      |fdn|
      |<moFdn>|
    Then The node will not contain the managed object <moFdn>:

    When Node <nodeName> is deleted
    When I wait 10 seconds
    When ManagedObject <subNetworkFdn> is deleted
    Then ManagedObject <subNetworkFdn> will not exist in DPS
    Then Node <nodeName> will not exist in DPS
    When I wait 10 seconds
    When I create a node called <nodeName>:
      | | |
    Then The node <nodeName> is SYNCHRONIZED

    Examples:
      | nodeName       | subNetworkFdn |moFdn|
      | LTE02ERBS00001 | SubNetwork=Ireland,SubNetwork=Westmeath |SubNetwork=Ireland,SubNetwork=Westmeath,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Ireland|

  Scenario Outline: Processing notifications of a node added using a different ossModelIdentity and ossPrefix.
    Given Node <nodeName> is deleted
    Then Node <nodeName> will not exist in DPS
    When I wait 10 seconds
    When I create a node called <nodeName>:
      |ossModelIdentity|17A-H.1.190|
      |ossPrefix|<subNetworkFdn>|
    When I enable cm supervision on the node <nodeName>
    Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
      | | |
    When Execute CREATE_MO netsim command:
      |fdn|moType|moName|
      |SubNetwork=Athlone,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Mjolnir|
    Then DPS will contain the MO <moFdn>:
      | | |
    When Execute DELETE_MO netsim command:
      |fdn|
      |<moFdn>|
    Then The node will not contain the managed object <moFdn>:
    When Node <nodeName> is deleted
    When I wait 10 seconds
    When ManagedObject <subNetworkFdn> is deleted
    Then ManagedObject <subNetworkFdn> will not exist in DPS
    Then Node <nodeName> will not exist in DPS
    When I create a node called <nodeName>:
      | | |

    Examples:
      | nodeName       | subNetworkFdn        |moFdn|
      | LTE02ERBS00001 | SubNetwork=Athlone   |SubNetwork=Athlone,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir|

  Scenario Outline: Processing notifications of a node that was synced previously with a different ossPrefix
    Given Node <nodeName> is deleted
    Then Node <nodeName> will not exist in DPS
    When I wait 10 seconds
    When I create a node called <nodeName>:
      |ossPrefix|<subNetworkFdn>|
    When I enable cm supervision on the node <nodeName>
    Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
      | | |
    When Execute CREATE_MO netsim command:
      |fdn|moType|moName|
      |SubNetwork=Ballymore,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1|Sctp|Mjolnir|
    Then DPS will contain the MO <moFdn>:
      | | |
    When Execute DELETE_MO netsim command:
      |fdn|
      |<moFdn>|
    Then The node will not contain the managed object <moFdn>:
    When Node <nodeName> is deleted
    When I wait 10 seconds
    When ManagedObject <subNetworkFdn> is deleted
    Then ManagedObject <subNetworkFdn> will not exist in DPS
    Then Node <nodeName> will not exist in DPS
    When I create a node called <nodeName>:
      | | |

    Examples:
      | nodeName       | subNetworkFdn        |moFdn|
      | LTE02ERBS00001 | SubNetwork=Ballymore   |SubNetwork=Ballymore,MeContext=LTE02ERBS00001,ManagedElement=1,TransportNetwork=1,Sctp=Mjolnir|
