#!/bin/bash

cd ~/Desktop/grafos

ls | grep -v -f ~/Desktop/arquivosSim > lista

xargs -t -L1 rm -f < lista
