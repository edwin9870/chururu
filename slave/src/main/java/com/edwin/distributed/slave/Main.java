package com.edwin.distributed.slave;

import com.edwin.distributed.slave.service.MerkleTreeService;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        MerkleTreeService merkleTreeService = new MerkleTreeService();
        merkleTreeService.sync();
    }

}
