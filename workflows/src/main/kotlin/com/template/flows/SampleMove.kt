package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@StartableByRPC
class SampleIssue (
        private val customerUUID: String,
        private val amount: Long,
        private val coinType: String
): FlowLogic<SignedTransaction>() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {

        // Lookup the account from UUID
        logger.debug("SampleIssue.kt Line 57. [1] accountInfo()")
        val customerAccountInfo = accountService.accountInfo(UUID.fromString(customerUUID))!!.state.data
        // get AnonymousParty
        logger.debug("SampleIssue.kt Line 60. [2] RequestKeyForAccount()")
        val holder = subFlow(RequestKeyForAccount(customerAccountInfo))
        // get Fungible token
        logger.debug("SampleIssue.kt Line 63. [3] getLocalHubInfo()")
        val identifier = coinType
        val fractionDigits = 0
        val token = TokenType(identifier, fractionDigits)
        val amountTokenType = amount of token issuedBy ourIdentity

        // create FungibleToken to Issue
        val fungibleToken = FungibleToken(amountTokenType, holder)

        // Issue Tokens
        return subFlow(IssueTokens(listOf(fungibleToken)))
    }
}