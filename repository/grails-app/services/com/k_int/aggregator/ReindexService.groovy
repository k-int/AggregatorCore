package com.k_int.aggregator

import com.k_int.aggregator.*

class ReindexService {

    def reindex_thread = null;
    def job_size = 0;

    def attemptReindex() {

        log.debug("attemptReindex");

        synchronized(this) {
            if ( reindex_thread == null ) {
                log.debug("Starting reindex thread....");
                def reindex_thread = Thread.start {
                    performReindex();
                }
            }
            else {
                log.debug("Already running");
            }
        }
        log.debug("Exit attemotReindex");
    }

    def performReindex() {
        log.debug("performReindex starting");
        def deposit_events = DepositEvent.list()
        job_size = deposit_events.size()
        log.debug("Processing ${job_size} deposit events...")
        deposit_events.each { de ->
            log.debug("processing ${de.depositToken} ${de.status} ${de.dateCreated} ${de.dataProvider?.code}");
        }
        job_size = 0;
        log.debug("performReindex complete");
    }

}
