package sbi.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.utilities.sumTokenStateAndRefs
import com.r3.corda.lib.tokens.contracts.utilities.sumTokenStateAndRefsOrNull
import com.r3.corda.lib.tokens.money.FiatCurrency.Companion.getInstance
import com.r3.corda.lib.tokens.selection.memory.internal.lookupExternalIdFromKey
import net.corda.core.contracts.Amount
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.Vault
import net.corda.core.node.services.Vault.RelevancyStatus
import net.corda.core.node.services.Vault.StateStatus
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.DEFAULT_PAGE_NUM
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria
import net.corda.core.serialization.CordaSerializable
import net.corda.core.utilities.ProgressTracker
import sbi.states.GSCoin
import java.security.PublicKey
import java.util.*


@StartableByRPC
@CordaSerializable
class GSCoinBalance(
        private val uuid: String): FlowLogic<Amount<IssuedTokenType>?>() {

    @Suspendable
    @Throws(FlowException::class)
    override fun call(): Amount<IssuedTokenType>? {
        // uuid
        val externalIds = mutableListOf<UUID>()
        externalIds.add(UUID.fromString(uuid))
        // contractStateType
        val contractStateTypes = mutableSetOf<Class<FungibleToken>>()
        contractStateTypes.add(FungibleToken::class.java)
        // create criteria
        val criteria = VaultQueryCriteria()
                .withContractStateTypes(contractStateTypes)
                .withExternalIds(externalIds)
        // query
        val vaultSnapshot = serviceHub.vaultService.queryBy<FungibleToken>(
                criteria, PageSpecification(DEFAULT_PAGE_NUM, 200)) // with Pagination
        return vaultSnapshot.states.sumTokenStateAndRefsOrNull()
    }

}
