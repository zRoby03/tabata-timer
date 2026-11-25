let releaseLock = null;
let releaseTimeout = null;

// The normal page and the miniature window use one timer (and one audio source) per browser profile.
export async function acquireTimer() {
  if (releaseLock) {
    clearTimeout(releaseTimeout);
    return true;
  }
  if (!navigator.locks) return true;
  return new Promise((resolve, reject) => {
    navigator.locks.request('tabata:running-timer', { ifAvailable: true }, lock => {
      if (!lock) { resolve(false); return; }
      const hold = new Promise(done => { releaseLock = done; });
      resolve(true);
      return hold;
    }).catch(reject);
  });
}

export function releaseTimer(afterMs = 0) {
  clearTimeout(releaseTimeout);
  if (!releaseLock) return;
  const release = () => {
    releaseLock?.();
    releaseLock = null;
  };
  if (afterMs) releaseTimeout = setTimeout(release, afterMs);
  else release();
}
