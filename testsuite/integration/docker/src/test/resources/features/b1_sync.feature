@Sync @RunAllTests
Feature: b1_Node Synchronization capabilities.

  Scenario Outline: Compares the topology tree of the node as defined in netsim and dps for a SYNCHRONIZED node.
    Given The node <nodeName> is SYNCHRONIZED
    When I read the netsim topology for node <nodeName>
    When I read the dps topology for node <nodeName>
    Then the topologies for node <nodeName> should match

    Examples:
      | nodeName       |
      | LTE02ERBS00001 |
