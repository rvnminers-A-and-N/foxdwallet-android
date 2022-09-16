//
//  ChainParams.h
//
//  Created by Aaron Voisine on 1/10/18.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

#ifndef ChainParams_h
#define ChainParams_h

#include "BRMerkleBlock.h"
#include "BRSet.h"
#include <assert.h>

static const int64_t COIN = 100000000;

// Burn Amounts
static const uint64_t IssueAssetBurnAmount = 500 * COIN;
static const uint64_t ReissueAssetBurnAmount = 100 * COIN;
static const uint64_t IssueSubAssetBurnAmount = 100 * COIN;
static const uint64_t IssueUniqueAssetBurnAmount = 5 * COIN;


typedef struct {
    uint32_t height;
    UInt256 hash;
    uint32_t timestamp;
    uint32_t target;
} CheckPoint;

typedef struct {
    const char *const *dnsSeeds; // NULL terminated array of dns seeds
    uint16_t standardPort;
    uint32_t magicNumber;
    uint64_t services;

    int (*verifyDifficulty)(const BRMerkleBlock *block, const BRSet *blockSet); // blockSet must have last 2016 blocks or 180 for DGW
    const CheckPoint *checkpoints;
    size_t checkpointsCount;
} ChainParams;

static const char *MainNetDNSSeeds[] = {
        "seed1.foxdcoin.com.", "seed2.foxdcoin.com.", "seed3.foxdcoin.com.", "seed4.foxdcoin.com.", NULL
};

static const char *TestNetDNSSeeds[] = {
        NULL
};

static const char *RegTestDNSSeeds[] = {
        "127.0.0.1", NULL
};

// blockchain checkpoints - these are also used as starting points for partial chain downloads, so they must be at
// difficulty transition boundaries in order to verify the block difficulty at the immediately following transition
static const CheckPoint MainNetCheckpoints[] = {
    {      0, "000000a26ea2c04148915028ac33daef2c004e2c2f45841a5af07ce0b57a9cf4", 1582102162, 0x1e00ffff },
    {      1, "00000457e73653668af20d7cdb4932c49576a2783ea729e16a9ebe833584f482", 1582116536, 0x1e0fffff },
    {      1, "00000457e73653668af20d7cdb4932c49576a2783ea729e16a9ebe833584f482", 1582116536, 0x1e0fffff },
    {      3, "000002c96c8a039acdee4a7a02f8d47b8dc057a14d14bdf02e64c908fa908296", 1582116544, 0x1e0fffff },
    {   5000, "000000649e183754abc34009b179fbf6201559411b14c6d2cd50ff92b5c6fbfd", 1582429607, 0x1e02786c },
    {  20000, "000000000bdec48ce19dedffc919a7f9fc7ec7a91d07af79961cb978fc6d4bed", 1583298994, 0x1c121ec0 }
};

static const CheckPoint TestNetCheckpoints[] = {
        NULL
};

static const CheckPoint RegTestCheckpoints[] = {
        {} // todo: retrieve using RPC call on local wallet!!
};

static int MainNetVerifyDifficulty(const BRMerkleBlock *block, const BRSet *blockSet) {
    const BRMerkleBlock *previous, *b = NULL;
    uint32_t i;

    assert(block != NULL);
    assert(blockSet != NULL);

    // check if we hit a difficulty transition, and find previous transition block
    if ((block->height % BLOCK_DIFFICULTY_INTERVAL) == 0) {
        for (i = 0, b = block; b && i < BLOCK_DIFFICULTY_INTERVAL; i++) {
            b = BRSetGet(blockSet, &b->prevBlock);
        }
    }

    previous = BRSetGet(blockSet, &block->prevBlock);
    return BRMerkleBlockVerifyDifficulty(block, previous, (b) ? b->timestamp : 0);
}

static int TestNetVerifyDifficulty(const BRMerkleBlock *block, const BRSet *blockSet) {
    return 1; // XXX skip testnet difficulty check for now
}

static int RegTestVerifyDifficulty(const BRMerkleBlock *block, const BRSet *blockSet) {
    return 1; // regtest diff check
}

// MainNet Burn Addresses
static const char strIssueAssetBurnAddressMainNet[] = "FDissueAssetXXXXXXXXXXXXXXXXarpEmt";
static const char strReissueAssetBurnAddressMainNet[] = "FDReissueAssetXXXXXXXXXXXXXXZAKPbJ";
static const char strIssueSubAssetBurnAddressMainNet[] = "FDissueSubAssetXXXXXXXXXXXXXW6D3dn";
static const char strIssueUniqueAssetBurnAddressMainNet[] = "FDissueUniqueAssetXXXXXXXXXXSkWCDd";
static const char strIssueMsgChannelAssetBurnAddressMainNet[] = "FDissueMsgChanneLAssetXXXXXXSkQjob";
static const char strIssueQualifierAssetBurnAddressMainNet[] = "FDissueQuaLifierXXXXXXXXXXXXZVc7zN";
static const char strIssueSubQualifierAssetBurnAddressMainNet[] = "FDissueSubQuaLifierXXXXXXXXXUhKn5T";
static const char strIssueRestrictedAssetBurnAddressMainNet[] = "FDissueRestrictedXXXXXXXXXXXXveZHA";
static const char strAddNullQualifierTagBurnAddressMainNet[] = "FDaddTagBurnXXXXXXXXXXXXXXXXaqLzue";
static const char strGlobalBurnAddressMainNet[] = "FDBurnXXXXXXXXXXXXXXXXXXXXXXWdW4er"; // Global Burn Address

// TestNet Burn Addresses
static const char strIssueAssetBurnAddressTestNet[] = "fXissueAssetXXXXXXXXXXXXXXXXZbEEFU";
static const char strReissueAssetBurnAddressTestNet[] = "fXReissueAssetXXXXXXXXXXXXXXYSUun2";
static const char strIssueSubAssetBurnAddressTestNet[] = "fXissueSubAssetXXXXXXXXXXXXXakqJkC";
static const char strIssueUniqueAssetBurnAddressTestNet[] = "fXissueUniqueAssetXXXXXXXXXXSycUW7";
static const char strIssueMsgChannelAssetBurnAddressTestNet[] = "fXissueMsgChanneLAssetXXXXXXRjh85D";
static const char strIssueQualifierAssetBurnAddressTestNet[] = "fXissueQuaLifierXXXXXXXXXXXXXAGDbp";
static const char strIssueSubQualifierAssetBurnAddressTestNet[] = "fXissueSubQuaLifierXXXXXXXXXXB1EjR";
static const char strIssueRestrictedAssetBurnAddressTestNet[] = "fXissueRestrictedXXXXXXXXXXXb6ZKVu";
static const char strAddNullQualifierTagBurnAddressTestNet[] = "fXaddTagBurnXXXXXXXXXXXXXXXXWYBj2e";
static const char strGlobalBurnAddressTestNet[] = "fXBurnXXXXXXXXXXXXXXXXXXXXXXX3XqfT"; // Global Burn Address

// RegTest Burn Addresses
static const char strIssueAssetBurnAddressRegTest[] = "n2issueAssetXXXXXXXXXXXXXXXXbfbi47";
static const char strReissueAssetBurnAddressRegTest[] = "n2ReissueAssetXXXXXXXXXXXXXXTxxPkJ";
static const char strIssueSubAssetBurnAddressRegTest[] = "n2issueSubAssetXXXXXXXXXXXXXVw16Ye";
static const char strIssueUniqueAssetBurnAddressRegTest[] = "n2issueUniqueAssetXXXXXXXXXXWmN3Qg";
static const char strIssueMsgChannelAssetBurnAddressRegTest[] = "n2issueMsgChanneLAssetXXXXXXW6188u";
static const char strIssueQualifierAssetBurnAddressRegTest[] = "n2issueQuaLifierXXXXXXXXXXXXVQXVcN";
static const char strIssueSubQualifierAssetBurnAddressRegTest[] = "n2issueSubQuaLifierXXXXXXXXXUAFxve";
static const char strIssueRestrictedAssetBurnAddressRegTest[] = "n1issueRestrictedXXXXXXXXXXXXZVT9V";
static const char strAddNullQualifierTagBurnAddressRegTest[] = "n2addTagBurnXXXXXXXXXXXXXXXXWQj6Et";
static const char strGlobalBurnAddressRegTest[] = "n2BurnXXXXXXXXXXXXXXXXXXXXXXV3KNcW"; // Global Burn Address

static const ChainParams MainNetParams = {
        MainNetDNSSeeds,
        8769,       // standardPort
        0x82abee93, // magicNumber
        0,          // services
        MainNetVerifyDifficulty,
        MainNetCheckpoints,
        sizeof(MainNetCheckpoints) / sizeof(*MainNetCheckpoints)
};

static const ChainParams TestNetParams = {
        TestNetDNSSeeds,
        18770,      // standardPort
        0x54a3a1e4, // magicNumber
        0,          // services
        TestNetVerifyDifficulty,
        TestNetCheckpoints,
        sizeof(TestNetCheckpoints) / sizeof(*TestNetCheckpoints)
};

static const ChainParams RegTestParams = {
        RegTestDNSSeeds,
        18444,      // standardPort
        0x8caffec6, // magicNumber
        0,          // services
        RegTestVerifyDifficulty,
        RegTestCheckpoints,
        sizeof(RegTestCheckpoints) / sizeof(*RegTestCheckpoints)
};
#endif // ChainParams_h
