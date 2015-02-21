# play-akka-cluster-websocket-chat

Multiroom scalable chat based on Play Framework 2 (Java) and Akka cluster

This this sample of integration a play framework 2 app (java) with akka cluster. It's provide a possibility to add new play node for scale system. when a new node added all nodes in the cluster share chat messages, no matter which node receives the message.

run node1:

activator -Dnode.id=1 -Dhttp.port=9000 -Dakka.remote.netty.tcp.port=2551 -Dakka.cluster.seed-nodes.0="akka.tcp://application@127.0.0.1:2551" run

run node2:

activator -Dnode.id=2 -Dhttp.port=9001 -Dakka.remote.netty.tcp.port=2552 -Dakka.cluster.seed-nodes.0="akka.tcp://application@127.0.0.1:2551" -Dakka.cluster.seed-nodes.1="akka.tcp://application@127.0.0.1:2552" run

run node3:

activator -Dnode.id=3 -Dhttp.port=9002 -Dakka.remote.netty.tcp.port=2553 -Dakka.cluster.seed-nodes.0="akka.tcp://application@127.0.0.1:2551" -Dakka.cluster.seed-nodes.1="akka.tcp://application@127.0.0.1:2552" -Dakka.cluster.seed-nodes.2="akka.tcp://application@127.0.0.1:2553" run
