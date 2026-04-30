#!/bin/bash
echo "Compiling IPC Simulator..."
mkdir -p out
javac -d out utils/Logger.java ipc/PipeProducer.java ipc/PipeConsumer.java ipc/PipeSimulation.java ipc/MessageQueue.java ipc/SharedMemory.java ipc/SemaphoreSimulation.java ipc/Signals.java ipc/Deadlock.java ipc/SocketIPC.java ui/SimulatorUI.java Main.java

java -cp out Main