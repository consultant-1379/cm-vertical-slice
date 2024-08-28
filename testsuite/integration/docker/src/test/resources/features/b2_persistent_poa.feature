@TestPersistentPoa @RunAllTests
Feature: b2_Persistence POA capabilities - Persistent POA specific tests.

  Scenario Outline: Verify a basic notification gets persisted in DPS indipendent of the receiving mscm instance.
    Given The node <nodeName> is SYNCHRONIZED
    Then I detect the supervising mscm for <nodeName>

    # Block LVS healthcheck on the supervising mscm to be sure notifications are routed to the other instance.
    When I block notifications on the supervising mscm
    When Execute SET_MO_ATTRIBUTES netsim command:
      | fdn     | attributeKeysValues |
      | <moFdn> | <netsimAttrValue1>  |
    When I wait 10 seconds
    Then DPS will contain <moFdn> with attribute name <attrName> and object value <dpsValue1>

    # Now block the instance which is receiving notifications, next notification will be lost.
    # The node will get a TCP RST and drop the subscription.
    When I unblock notifications on the supervising mscm
    When I block notifications on the other mscm
    When Execute SET_MO_ATTRIBUTES netsim command:
      | fdn     | attributeKeysValues |
      | <moFdn> | <netsimAttrValue2>  |
    When I wait 10 seconds
    Then DPS will contain <moFdn> with attribute name <attrName> and object value <dpsValue1>

    # Force a subscription validation, a DELTA sync will be performed and notifications restored.
    When I send validation for <nodeName>
    When I wait 30 seconds
    Then DPS will contain <moFdn> with attribute name <attrName> and object value <dpsValue2>
    When Execute SET_MO_ATTRIBUTES netsim command:
      | fdn     | attributeKeysValues |
      | <moFdn> | <netsimAttrValue3>  |
    When I wait 10 seconds
    Then DPS will contain <moFdn> with attribute name <attrName> and object value <dpsValue3>

    Examples:
      | nodeName       | moFdn                                                                                     | attrName  | netsimAttrValue1       | dpsValue1    | netsimAttrValue2       | dpsValue2    | netsimAttrValue3       | dpsValue3    |
      | LTE02ERBS00001 | MeContext=LTE02ERBS00001,ManagedElement=1,ENodeBFunction=1,EUtranCellFDD=LTE02ERBS00001-1 | userLabel | userLabel=Vulcanians-1 | Vulcanians-1 | userLabel=Vulcanians-2 | Vulcanians-2 | userLabel=Vulcanians-3 | Vulcanians-3 |
