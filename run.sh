#!/bin/bash
echo "Compiling IPC Simulator..."
mkdir -p out
javac -d out utils/Logger.java ipc/PipeSimulation.java ipc/MessageQueue.java ipc/SharedMemory.java ipc/SemaphoreSimulation.java ipc/Signals.java ipc/Deadlock.java ui/SimulatorUI.java Main.java
echo "Running..."
java -cp out Main