@AddAndSyncNode
Feature: a_Set up for the tests.

  # SetupPib sets up pib configuration parameters
  # SetupNetsim generates the network map
  # SetupDpsObjectTypesConversion helps conversion of dps object types
  #
  @SetupPib
  @SetupNetsim
  @SetupDpsObjectTypesConversion
  Scenario Outline: Create, Supervise and Synchronize a node.
    When I create a node called <nodeName>:
      |  |  |
    When I enable cm supervision on the node <nodeName>
    Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
      |  |  |

    Examples:
      | nodeName       |
      | LTE02ERBS00001 |
