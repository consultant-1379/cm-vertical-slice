@Robust @RunAllTests
Feature: e_Verify Robustness of the System.

#  # @BytemanTearDown unloads all the byteman rules
#  #
#  @BytemanTearDown
#  Scenario Outline: Sync Retries and Fails if an Exception is thrown retrieving the Generation Counter.
#    Given A MociConnectionProviderException is thrown for:
#      | RULE         | Throw MociConnectionProviderException when retrieving generation counter\n   |
#      | CLASS        | com.ericsson.nms.mediation.component.moci.handlers.SyncNodeMociTopologyHandler\n |
#      | METHOD       | retrieveGenerationCounter(com.ericsson.nms.mediation.component.moci.operations.SyncNodeMociContext)\n |
#      | IF           | true\n |
#      | DO           | throw new MociConnectionProviderException("Unable to retrieve generation counter for node")\n |
#      | ENDRULE      | \n |
#    When The action sync is invoked for the managed object NetworkElement=LTE02ERBS00001,CmFunction=1
#    Then The syncstatus for node <nodeName> will be TOPOLOGY:
#      | waitTimeMinutes         | 3 |
#      | waitIntervalMilliseconds | 1 |
#    Then The syncstatus for node <nodeName> will be UNSYNCHRONIZED:
#      | waitTimeMinutes         | 3 |
#      | waitIntervalMilliseconds | 1 |
#    Then The syncstatus for node <nodeName> will be TOPOLOGY:
#      | waitTimeMinutes         | 3 |
#      | waitIntervalMilliseconds | 1 |
#    Then The syncstatus for node <nodeName> will be UNSYNCHRONIZED:
#      | waitTimeMinutes         | 3 |
#      | waitIntervalMilliseconds | 1 |
#
#    Examples:
#      | nodeName |
#      | LTE02ERBS00001 |

#  @ResetPib
#  @ResumeJms
#  Scenario Outline: Attempt Resubscription after 1 HB if the Subscription Creation fails.
#    Given The cm_policy_heartbeat_interval_timer_value_millisecs PIB parameter is updated to 30000
#    When I disable cm supervision on the node <nodeName>
#    When A pause operation is invoked on jms-queue name ClusteredNetworkElementSubscriptions
#    When I enable cm supervision on the node <nodeName>
#    Then The syncstatus for node <nodeName> will be UNSYNCHRONIZED:
#      | | |
#    When A remove-messages operation is invoked on jms-queue name ClusteredNetworkElementSubscriptions
#    When A resume operation is invoked on jms-queue name ClusteredNetworkElementSubscriptions
#    Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
#      | | |
#
#    Examples:
#      | nodeName |
#      | LTE02ERBS00001 |

#  # @ResumeJms resumes the Jms queues which were paused
#  #
#  @ResumeJms
#  Scenario Outline: Confirm Node is PENDING if JMS Queue is Paused.
#    Given The node <nodeName> is UNSYNCHRONIZED
#    When I enable cm supervision on the node <nodeName>
#    When A pause operation is invoked on jms-queue name ClusteredMediationServiceConsumerCM0
#    Then The syncstatus for node <nodeName> will be UNSYNCHRONIZED:
#      | | |
#    When A resume operation is invoked on jms-queue name ClusteredMediationServiceConsumerCM0
#    Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
#      | | |
#
#    Examples:
#      | nodeName |
#      | LTE02ERBS00001 |

  # @BytemanTearDown unloads all the byteman rules
  # @ResetPib restores PIB configurations in medcore
  #
  @ResetPib
  @BytemanTearDown
  Scenario Outline: Verify that CM Supervision Feature activation attempt fails gracefully if HTTP connectivity cannot be established towards node.
    Given The cm_policy_heartbeat_interval_timer_value_millisecs PIB parameter is updated to 30000
    Given The node <nodeName> is UNSYNCHRONIZED
    When A CorbaException is thrown for:
      | RULE         | Throw CorbaException when retrieving IOR\n   |
      | CLASS        | com.ericsson.oss.mediation.network.providers.ior.HttpIorClient\n |
      | METHOD       | getIOR(String)\n |
      | IF           | true\n |
      | DO           | throw new CorbaException("Unable to retrieve IOR for node")\n |
      | ENDRULE      | \n |
    When I enable cm supervision on the node <nodeName>
    When I wait 5 seconds
    Then The syncstatus for node <nodeName> will be UNSYNCHRONIZED:
      | | |
    When I unload all Byteman Rules
    Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
      | | |

    Examples:
      | nodeName |
      | LTE02ERBS00001 |

  # @BytemanTearDown unloads all the byteman rules
  # @ResetPib restores PIB configurations in medcore
  #
  @BytemanTearDown
  @ResetPib
  Scenario Outline: Attempt to Resubscribe if CORBA Exception is thrown validating the Node Subscription.
    Given The cm_policy_heartbeat_interval_timer_value_millisecs PIB parameter is updated to 30000
    #Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
    When The node <nodeName> is SYNCHRONIZED
    When A MARSHAL Exception is thrown for:
      | RULE         | Throw CORBA.MARSHAL when validating node subscription\n   |
      | INTERFACE    | com.ericsson.nms.umts.ranos.cms.nead.segmentserver.neaccess.cello_p1.idl.ConfigExtended.NotificationProducerOperations\n |
      | METHOD       | get_subscription_status(int)\n |
      | IF           | true\n |
      | DO           | throw new org.omg.CORBA.MARSHAL("CORBA.MARSHAL: Node Subscription Validation Failed.")\n |
      | ENDRULE      | \n |
    Then The syncstatus for node <nodeName> will be UNSYNCHRONIZED:
      | waitTimeMinutes         | 2 |
      | waitIntervalMilliseconds | 1 |
    When I unload all Byteman Rules
    Then The syncstatus for node <nodeName> will be SYNCHRONIZED:
      | | |

    Examples:
      | nodeName |
      | LTE02ERBS00001 |
